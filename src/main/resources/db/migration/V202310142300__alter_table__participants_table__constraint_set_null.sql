ALTER TABLE participants_table
    DROP CONSTRAINT fk__participants_table__tournaments_table;

ALTER TABLE participants_table
    ADD CONSTRAINT fk__participants_table__tournaments_table
        FOREIGN KEY (tournament_id) REFERENCES tournaments_table
            ON DELETE SET NULL;

ALTER TABLE participants_table
    DROP CONSTRAINT fk__participants_table__users_table;

ALTER TABLE participants_table
    ADD CONSTRAINT fk__participants_table__users_table
        FOREIGN KEY (user_id) REFERENCES users_table
            ON DELETE SET NULL;
