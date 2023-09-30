CREATE TABLE IF NOT EXISTS badges_table(
    id          UUID NOT NULL PRIMARY KEY,
    description VARCHAR(255),
    picture_url VARCHAR(255),
    titled      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS tournaments_table(
    date   TIMESTAMP(6),
    id     UUID NOT NULL PRIMARY KEY,
    status VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS rounds_table(
    is_finished   BOOLEAN,
    number        INTEGER,
    id            UUID NOT NULL PRIMARY KEY,
    tournament_id UUID CONSTRAINT fk__rounds_table__tournaments_table REFERENCES tournaments_table
);

CREATE TABLE IF NOT EXISTS users_table (
    id       UUID NOT NULL PRIMARY KEY,
    name     VARCHAR(255),
    password VARCHAR(255),
    provider VARCHAR(255),
    username VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS matches_table(
    id          UUID NOT NULL PRIMARY KEY,
    player_id_1 UUID CONSTRAINT fk__matches_table__users_table1 REFERENCES users_table,
    player_id_2 UUID CONSTRAINT fk__matches_table__users_table2 REFERENCES users_table,
    round_id    UUID CONSTRAINT fk__matches_table__round_table REFERENCES rounds_table,
    result      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS participants_table(
    buchholz      NUMERIC(38, 2),
    score         NUMERIC(38, 2),
    id            UUID NOT NULL PRIMARY KEY,
    tournament_id UUID CONSTRAINT fk__participants_table__tournaments_table REFERENCES tournaments_table,
    user_id       UUID CONSTRAINT fk__participants_table__users_table REFERENCES users_table,
    nickname      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users_badges_table(
    badge_id UUID CONSTRAINT fk__user_badges__badges_table REFERENCES badges_table,
    id       UUID NOT NULL PRIMARY KEY,
    user_id  UUID CONSTRAINT fk__user_badges__users_table REFERENCES users_table
);
