CREATE TABLE IF NOT EXISTS tournament_events_table(
    id UUID NOT NULL PRIMARY KEY,
    name VARCHAR,
    location_name VARCHAR,
    location_url VARCHAR,
    date TIMESTAMP(6),
    status VARCHAR(255),
    rounds_number INTEGER NOT NULL,
    registration_limit INTEGER,
    schedule_id UUID,
    created_at TIMESTAMP DEFAULT (NOW() AT TIME ZONE 'UTC'),
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);