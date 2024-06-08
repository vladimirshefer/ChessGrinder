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
VALUES('d1dea6e7-a60f-41a5-b53b-bfb8bdc69b9d', 'DEFAULT CLUB', 'DEFAULT DESCRIPTION', 'DEFAULT LOCATION');
