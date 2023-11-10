CREATE TABLE user_reputation_history_table
(
    id         uuid                    NOT NULL
        CONSTRAINT user_reputation_history_table_pk
            PRIMARY KEY,
    user_id    uuid                    NOT NULL
        CONSTRAINT user_reputation_history_table_users_table_fk
            REFERENCES users_table,
    amount     INTEGER                 NOT NULL,
    comment    VARCHAR                 NOT NULL,
    created_at TIMESTAMP DEFAULT NOW() NOT NULL,
    updated_at TIMESTAMP
);

