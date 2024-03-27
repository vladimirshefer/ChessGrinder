ALTER TABLE users_table
    ADD COLUMN created_at TIMESTAMP DEFAULT NOW(),
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by VARCHAR(255),
    ADD COLUMN updated_by VARCHAR(255);
UPDATE users_table SET created_by = username;
ALTER TABLE users_table ALTER COLUMN created_by SET NOT NULL;

ALTER TABLE users_roles_table
    ADD COLUMN created_at TIMESTAMP DEFAULT NOW(),
    ADD COLUMN updated_at TIMESTAMP,
    ADD COLUMN created_by VARCHAR(255),
    ADD COLUMN updated_by VARCHAR(255);
