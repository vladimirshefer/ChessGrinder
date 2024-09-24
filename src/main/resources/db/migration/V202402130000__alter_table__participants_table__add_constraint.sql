DELETE FROM participants_table WHERE tournament_id IS NULL;

ALTER TABLE participants_table
    DROP CONSTRAINT fk__participants_table__tournaments_table;

ALTER TABLE participants_table
    ADD CONSTRAINT fk__participants_table__tournaments_table
        FOREIGN KEY (tournament_id) REFERENCES tournaments_table
            ON DELETE CASCADE;

ALTER TABLE participants_table
    ADD CONSTRAINT unique_id_tournament_id UNIQUE (id, tournament_id);

ALTER TABLE participants_table
    ALTER tournament_id SET NOT NULL;
