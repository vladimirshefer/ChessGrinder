ALTER TABLE users_table ADD COLUMN created_at TIMESTAMP DEFAULT (NOW() AT TIME ZONE 'UTC');
ALTER TABLE users_table ADD COLUMN updated_at TIMESTAMP;
ALTER TABLE users_table ADD COLUMN created_by VARCHAR(255);
ALTER TABLE users_table ADD COLUMN updated_by VARCHAR(255);
UPDATE users_table SET created_by = username;

ALTER TABLE users_roles_table ADD COLUMN created_at TIMESTAMP DEFAULT (NOW() AT TIME ZONE 'UTC');
ALTER TABLE users_roles_table ADD COLUMN updated_at TIMESTAMP;
ALTER TABLE users_roles_table ADD COLUMN created_by VARCHAR(255);
ALTER TABLE users_roles_table ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE user_reputation_history_table ADD COLUMN created_by VARCHAR(255);
ALTER TABLE user_reputation_history_table ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE users_badges_table ADD COLUMN created_at TIMESTAMP DEFAULT (NOW() AT TIME ZONE 'UTC');
ALTER TABLE users_badges_table ADD COLUMN updated_at TIMESTAMP;
ALTER TABLE users_badges_table ADD COLUMN created_by VARCHAR(255);
ALTER TABLE users_badges_table ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE tournaments_table ADD COLUMN created_at TIMESTAMP DEFAULT (NOW() AT TIME ZONE 'UTC');
ALTER TABLE tournaments_table ADD COLUMN updated_at TIMESTAMP;
ALTER TABLE tournaments_table ADD COLUMN created_by VARCHAR(255);
ALTER TABLE tournaments_table ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE rounds_table ADD COLUMN created_at TIMESTAMP DEFAULT (NOW() AT TIME ZONE 'UTC');
ALTER TABLE rounds_table ADD COLUMN updated_at TIMESTAMP;
ALTER TABLE rounds_table ADD COLUMN created_by VARCHAR(255);
ALTER TABLE rounds_table ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE roles_table ADD COLUMN created_at TIMESTAMP DEFAULT (NOW() AT TIME ZONE 'UTC');
ALTER TABLE roles_table ADD COLUMN updated_at TIMESTAMP;
ALTER TABLE roles_table ADD COLUMN created_by VARCHAR(255);
ALTER TABLE roles_table ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE participants_table ADD COLUMN created_at TIMESTAMP DEFAULT (NOW() AT TIME ZONE 'UTC');
ALTER TABLE participants_table ADD COLUMN updated_at TIMESTAMP;
ALTER TABLE participants_table ADD COLUMN created_by VARCHAR(255);
ALTER TABLE participants_table ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE matches_table ADD COLUMN created_at TIMESTAMP DEFAULT (NOW() AT TIME ZONE 'UTC');
ALTER TABLE matches_table ADD COLUMN updated_at TIMESTAMP;
ALTER TABLE matches_table ADD COLUMN created_by VARCHAR(255);
ALTER TABLE matches_table ADD COLUMN updated_by VARCHAR(255);

ALTER TABLE badges_table ADD COLUMN created_at TIMESTAMP DEFAULT (NOW() AT TIME ZONE 'UTC');
ALTER TABLE badges_table ADD COLUMN updated_at TIMESTAMP;
ALTER TABLE badges_table ADD COLUMN created_by VARCHAR(255);
ALTER TABLE badges_table ADD COLUMN updated_by VARCHAR(255);
