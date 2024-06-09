CREATE TABLE clubs_table
(
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR NOT NULL,
    location VARCHAR NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    UNIQUE(name, location)
);

INSERT INTO clubs_table (id, name, description, location)
VALUES('12345678-9abc-def0-1234-56789abcdef0', 'DEFAULT CLUB', 'DEFAULT DESCRIPTION', 'DEFAULT LOCATION');
