CREATE INDEX idx_rounds_table_tournament_id_number
    ON rounds_table (tournament_id, number);

CREATE INDEX idx_participants_table_tournament_id
    ON participants_table (tournament_id);

CREATE INDEX idx_participants_table_user_id
    ON participants_table (user_id);

CREATE INDEX idx_matches_table_round_id
    ON matches_table (round_id);

CREATE INDEX idx_matches_table_participant_id_1
    ON matches_table (participant_id_1);

CREATE INDEX idx_matches_table_participant_id_2
    ON matches_table (participant_id_2);

CREATE INDEX idx_user_reputation_history_table_user_id
    ON user_reputation_history_table (user_id);

CREATE INDEX idx_users_badges_table_badge_id
    ON users_badges_table (badge_id);

CREATE INDEX idx_users_badges_table_user_id
    ON users_badges_table (user_id);

CREATE INDEX idx_users_roles_table_role_id
    ON users_roles_table (role_id);

CREATE INDEX idx_users_roles_table_user_id
    ON users_roles_table (user_id);
