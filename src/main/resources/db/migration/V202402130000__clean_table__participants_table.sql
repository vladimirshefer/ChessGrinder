DROP PROCEDURE IF EXISTS delete_participants_without_tournament_id;

CREATE PROCEDURE delete_participants_without_tournament_id()
LANGUAGE plpgsql
AS $$
BEGIN
    RAISE NOTICE 'Begin cleaning the table!';
    DELETE FROM participants_table WHERE tournament_id IS NULL;
END;
$$;

CALL delete_participants_without_tournament_id();
DROP PROCEDURE delete_participants_without_tournament_id;
