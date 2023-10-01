CREATE UNIQUE INDEX users_roles_table_user_id_role_id_uindex
    ON users_roles_table (user_id, role_id);
