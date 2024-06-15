ALTER TABLE tournaments_table
    ADD COLUMN club_id UUID,
    ADD CONSTRAINT fk__tournaments_table__clubs_table
        FOREIGN KEY (club_id) REFERENCES clubs_table(id)
            ON DELETE CASCADE;

UPDATE tournaments_table
SET club_id = 'd1dea6e7-a60f-41a5-b53b-bfb8bdc69b9d'; -- default club

ALTER TABLE tournaments_table
ALTER COLUMN club_id SET NOT NULL;
