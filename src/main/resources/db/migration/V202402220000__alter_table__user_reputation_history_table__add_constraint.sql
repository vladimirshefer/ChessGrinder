ALTER TABLE user_reputation_history_table
    DROP CONSTRAINT user_reputation_history_table_users_table_fk;

ALTER TABLE user_reputation_history_table
    ADD CONSTRAINT fk__user_reputation_history_table__users_table
        FOREIGN KEY (user_id) REFERENCES users_table
            ON DELETE CASCADE;
