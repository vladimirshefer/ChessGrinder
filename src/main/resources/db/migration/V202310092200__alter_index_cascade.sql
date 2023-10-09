ALTER TABLE matches_table
    DROP CONSTRAINT fk__matches_table__users_table2;

ALTER TABLE matches_table
    ADD CONSTRAINT fk__matches_table__users_table2
        FOREIGN KEY (player_id_2) REFERENCES users_table
            ON DELETE CASCADE;

ALTER TABLE matches_table
    DROP CONSTRAINT fk__matches_table__round_table;

ALTER TABLE matches_table
    ADD CONSTRAINT fk__matches_table__round_table
        FOREIGN KEY (round_id) REFERENCES rounds_table
            ON DELETE CASCADE;

ALTER TABLE matches_table
    DROP CONSTRAINT fk__matches_table__users_table1;

ALTER TABLE matches_table
    ADD CONSTRAINT fk__matches_table__users_table1
        FOREIGN KEY (player_id_1) REFERENCES users_table
            ON DELETE CASCADE;

ALTER TABLE participants_table
    DROP CONSTRAINT fk__participants_table__tournaments_table;

ALTER TABLE participants_table
    ADD CONSTRAINT fk__participants_table__tournaments_table
        FOREIGN KEY (tournament_id) REFERENCES tournaments_table
            ON DELETE CASCADE;

ALTER TABLE participants_table
    DROP CONSTRAINT fk__participants_table__users_table;

ALTER TABLE participants_table
    ADD CONSTRAINT fk__participants_table__users_table
        FOREIGN KEY (user_id) REFERENCES users_table
            ON DELETE CASCADE;

ALTER TABLE rounds_table
    DROP CONSTRAINT fk__rounds_table__tournaments_table;

ALTER TABLE rounds_table
    ADD CONSTRAINT fk__rounds_table__tournaments_table
        FOREIGN KEY (tournament_id) REFERENCES tournaments_table
            ON DELETE CASCADE;

ALTER TABLE users_badges_table
    DROP CONSTRAINT fk__user_badges__badges_table;

ALTER TABLE users_badges_table
    ADD CONSTRAINT fk__user_badges__badges_table
        FOREIGN KEY (badge_id) REFERENCES badges_table
            ON DELETE CASCADE;

ALTER TABLE users_badges_table
    DROP CONSTRAINT fk__user_badges__users_table;

ALTER TABLE users_badges_table
    ADD CONSTRAINT fk__user_badges__users_table
        FOREIGN KEY (user_id) REFERENCES users_table
            ON DELETE CASCADE;

ALTER TABLE users_roles_table
    DROP CONSTRAINT ft__users_roles_table__roles_table;

ALTER TABLE users_roles_table
    ADD CONSTRAINT ft__users_roles_table__roles_table
        FOREIGN KEY (role_id) REFERENCES roles_table
            ON DELETE CASCADE;

ALTER TABLE users_roles_table
    DROP CONSTRAINT ft__users_roles_table__users_table;

ALTER TABLE users_roles_table
    ADD CONSTRAINT ft__users_roles_table__users_table
        FOREIGN KEY (user_id) REFERENCES users_table
            ON DELETE CASCADE;
