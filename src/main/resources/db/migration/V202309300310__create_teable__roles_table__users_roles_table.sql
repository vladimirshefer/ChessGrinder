CREATE TABLE IF NOT EXISTS roles_table(
    id   UUID NOT NULL PRIMARY KEY,
    name VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS users_roles_table(
    id      UUID NOT NULL PRIMARY KEY,
    role_id UUID NOT NULL CONSTRAINT ft__users_roles_table__roles_table REFERENCES roles_table,
    user_id UUID NOT NULL CONSTRAINT ft__users_roles_table__users_table REFERENCES users_table
);
