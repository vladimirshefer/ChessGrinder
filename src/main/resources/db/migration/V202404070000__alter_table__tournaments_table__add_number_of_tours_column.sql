ALTER TABLE tournaments_table
    ADD COLUMN rounds_number INTEGER;
UPDATE tournaments_table AS t
SET rounds_number = (
    SELECT COUNT(*)
    FROM rounds_table AS r
    WHERE r.tournament_id = t.id
);
ALTER TABLE tournaments_table
    ALTER COLUMN rounds_number SET NOT NULL;
