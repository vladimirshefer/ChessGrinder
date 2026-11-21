ALTER TABLE tournaments_table
    ADD COLUMN IF NOT EXISTS serial_number INT;

ALTER TABLE tournaments_table
    ADD COLUMN IF NOT EXISTS bracket VARCHAR(255);
