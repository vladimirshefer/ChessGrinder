CREATE TABLE IF NOT EXISTS tournament_event_schedules_table(
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    day_of_week INTEGER,
    time TIME,
    status VARCHAR(255),
    created_at TIMESTAMP DEFAULT (NOW() AT TIME ZONE 'UTC'),
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

-- Add foreign key constraint to tournament_events_table
ALTER TABLE tournament_events_table
    ADD CONSTRAINT fk__tournament_events_table__tournament_event_schedules_table
    FOREIGN KEY (schedule_id) REFERENCES tournament_event_schedules_table(id);