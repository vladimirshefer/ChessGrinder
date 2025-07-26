ALTER TABLE tournaments_table
    ADD COLUMN event_id UUID;

ALTER TABLE tournaments_table
    ADD CONSTRAINT fk__tournaments_table__tournament_events_table
    FOREIGN KEY (event_id) REFERENCES tournament_events_table(id);
