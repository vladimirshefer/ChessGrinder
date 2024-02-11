DROP PROCEDURE IF EXISTS fill_participants_table;
CREATE PROCEDURE fill_participants_table()
LANGUAGE plpgsql
AS $$
    DECLARE participant_id UUID;
    DECLARE tournamentId UUID;
BEGIN
    RAISE NOTICE 'Begin place inserting!';
    -- Iterating through every existing tournament
    FOR tournamentId IN (SELECT tournament_id FROM participants_table)
    LOOP
        -- Iterating through every participant of tournament
        FOR participant_id IN (
            SELECT id FROM participants_table pt
            WHERE pt.tournament_id = tournamentId
        )
        LOOP
            WITH ranked_participants AS (
                SELECT
                    id AS participant_id,
                    DENSE_RANK() OVER (ORDER BY score DESC, buchholz DESC, nickname DESC) AS ranked_place
                FROM participants_table pt
                WHERE pt.tournament_id = tournamentId
            )
            -- Putting places to participants
            UPDATE participants_table AS pt
                SET place = rp.ranked_place
                FROM ranked_participants AS rp
                WHERE pt.id = rp.participant_id AND pt.tournament_id = tournamentId;
        END LOOP;
    END LOOP;
END;
$$;

CALL fill_participants_table();
DROP PROCEDURE fill_participants_table;
