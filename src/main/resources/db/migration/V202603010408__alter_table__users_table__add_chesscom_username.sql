-- Add nullable chesscom username column for users
ALTER TABLE users_table
    ADD COLUMN IF NOT EXISTS chesscom_username VARCHAR(255);
