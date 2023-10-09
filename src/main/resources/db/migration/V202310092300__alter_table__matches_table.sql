DELETE FROM matches_table;

ALTER TABLE matches_table
    ADD participant_id_1 uuid;

ALTER TABLE matches_table
    ADD participant_id_2 uuid;

ALTER TABLE matches_table
    ADD CONSTRAINT fk__matches_table__participants_table1
        FOREIGN KEY (participant_id_1) REFERENCES participants_table
            ON DELETE CASCADE;

ALTER TABLE matches_table
    ADD CONSTRAINT fk__matches_table__participants_table2
        FOREIGN KEY (participant_id_2) REFERENCES participants_table
            ON DELETE CASCADE;

ALTER TABLE matches_table
    DROP CONSTRAINT fk__matches_table__users_table2;

ALTER TABLE matches_table
    DROP COLUMN player_id_2;

ALTER TABLE matches_table
    DROP CONSTRAINT fk__matches_table__users_table1;

ALTER TABLE matches_table
    DROP COLUMN player_id_1;

