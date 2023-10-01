CREATE UNIQUE INDEX participants_table_tournament_id_user_id_uindex
    ON participants_table (tournament_id, user_id);
