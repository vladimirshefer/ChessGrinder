ALTER TABLE users_table
    ADD COLUMN IF NOT EXISTS usertag VARCHAR (64);

CREATE UNIQUE INDEX IF NOT EXISTS users_table_usertag_uindex ON users_table (usertag);
