CREATE UNIQUE INDEX users_badges_table_user_id_badge_id_uindex
    ON users_badges_table (user_id, badge_id);
