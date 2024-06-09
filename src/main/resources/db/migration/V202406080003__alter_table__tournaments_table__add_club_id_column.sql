ALTER TABLE tournaments_table
    ADD COLUMN club_id UUID,
    ADD CONSTRAINT fk__tournaments_table__clubs_table
        FOREIGN KEY (club_id) REFERENCES clubs_table(id)
            ON DELETE CASCADE;

UPDATE tournaments_table
SET club_id = '12345678-9abc-def0-1234-56789abcdef0'; -- default club

ALTER TABLE tournaments_table
ALTER COLUMN club_id SET NOT NULL;
