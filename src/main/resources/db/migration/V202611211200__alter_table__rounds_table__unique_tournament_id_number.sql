DROP INDEX IF EXISTS idx_rounds_table_tournament_id_number;

CREATE UNIQUE INDEX idx_rounds_table_tournament_id_number
    ON rounds_table (tournament_id, number);
