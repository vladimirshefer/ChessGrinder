ALTER TABLE tournaments_table
    ADD COLUMN number_of_rounds INTEGER;
UPDATE tournaments_table AS t
SET number_of_rounds = (
    SELECT COUNT(*)
    FROM rounds_table AS r
    WHERE r.tournament_id = t.id
);
ALTER TABLE tournaments_table
    ALTER COLUMN number_of_rounds SET NOT NULL;
