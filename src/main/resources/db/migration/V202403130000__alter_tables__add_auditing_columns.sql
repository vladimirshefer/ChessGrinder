ALTER TABLE users_table
    ADD COLUMN created_at TIMESTAMPTZ DEFAULT NOW(),
    ADD COLUMN updated_at TIMESTAMPTZ,
    ADD COLUMN created_by UUID,
    ADD COLUMN updated_by UUID,
    ADD CONSTRAINT fk__users_table__created_by
        FOREIGN KEY (created_by) REFERENCES users_table(id)
            ON DELETE SET NULL,
    ADD CONSTRAINT fk__users_table__updated_by
        FOREIGN KEY (updated_by) REFERENCES users_table(id)
            ON DELETE SET NULL;
UPDATE users_table SET created_by = id;
