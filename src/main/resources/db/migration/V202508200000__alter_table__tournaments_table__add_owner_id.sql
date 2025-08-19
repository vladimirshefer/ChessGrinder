-- Add owner_id column to tournaments_table and create foreign key to users_table
ALTER TABLE tournaments_table ADD COLUMN IF NOT EXISTS owner_id UUID;

ALTER TABLE tournaments_table
    ADD CONSTRAINT fk_tournaments_table_owner
        FOREIGN KEY (owner_id) REFERENCES users_table(id)
            ON DELETE SET NULL ;
