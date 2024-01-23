ALTER TABLE users_table
    ADD email VARCHAR;

UPDATE users_table
SET email = users_table.username
WHERE TRUE;

