ALTER TABLE tournaments_table
    ADD COLUMN club_id UUID,
    ADD CONSTRAINT fk__tournaments_table__clubs_table
        FOREIGN KEY (club_id) REFERENCES clubs_table(id)
            ON DELETE CASCADE;
