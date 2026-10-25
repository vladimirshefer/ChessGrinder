-- Add nullable active city column for users: the city where the user plays most
ALTER TABLE users_table
    ADD COLUMN IF NOT EXISTS active_city VARCHAR(255);

-- Backfill once from the city of the latest finished tournament the user participated in
UPDATE users_table
SET active_city = (
    SELECT t.city
    FROM participants_table p JOIN tournaments_table t ON t.id = p.tournament_id
    WHERE p.user_id = users_table.id
      AND t.status = 'FINISHED'
      AND t.city IS NOT NULL
    ORDER BY t.date DESC
    LIMIT 1
);
