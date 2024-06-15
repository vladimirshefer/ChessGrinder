CREATE TABLE subscriptions_table (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    club_id UUID NOT NULL,
    subscription_level_id UUID NOT NULL,
    start_date TIMESTAMP NOT NULL,
    finish_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT (NOW() AT TIME ZONE 'UTC'),
    updated_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    UNIQUE(user_id, club_id),
    FOREIGN KEY (user_id) REFERENCES users_table(id) ON DELETE CASCADE,
    FOREIGN KEY (club_id) REFERENCES clubs_table(id) ON DELETE CASCADE,
    FOREIGN KEY (subscription_level_id) REFERENCES subscription_levels_table(id) ON DELETE CASCADE
);
