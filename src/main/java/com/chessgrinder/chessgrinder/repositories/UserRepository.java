package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UserRepository extends PagingAndSortingRepository<UserEntity, UUID>, CrudRepository<UserEntity, UUID> {

    @Override
    List<UserEntity> findAll();

    UserEntity findByUsername(String userName);

    @Modifying
    @Query("UPDATE UserEntity u SET u.reputation = u.reputation + :amount WHERE u.id = :userId")
    void addReputation(UUID userId, Integer amount);

    @Query("SELECT ub.user from UserBadgeEntity ub WHERE ub.badge.id = :badgeId")
    List<UserEntity> findAllByBadgeId(UUID badgeId);


    @Query("SELECT SUM(p.score) " +
            "FROM ParticipantEntity p " +
            "WHERE p.user.id = :userId " +
            "AND p.tournament.date >= :globalScoreFromDate " +
            "AND p.tournament.date <= :globalScoreToDate " +
            "AND p.tournament.status = 'FINISHED'")
    BigDecimal getGlobalScore(
            UUID userId,
            LocalDateTime globalScoreFromDate,
            LocalDateTime globalScoreToDate
    );

    @Modifying
    @Query("UPDATE UserEntity u SET u.eloPoints = 0")
    void clearAllEloPoints();

    @Query(value = """
        WITH user_ids AS (
            SELECT :comparableUserId AS comparable_user_id,
                   :opponentUserId AS opponent_user_id
        ),
        user_participants AS (
            SELECT p.id AS participant_id, p.user_id
            FROM participants_table AS p
            JOIN user_ids AS u ON p.user_id IN (u.comparable_user_id, u.opponent_user_id)
        ),
        matches AS (
            SELECT
                m.result,
                p1.user_id AS user1_id,
                p2.user_id AS user2_id
            FROM matches_table AS m
            JOIN user_participants AS p1 ON m.participant_id_1 = p1.participant_id
            JOIN user_participants AS p2 ON m.participant_id_2 = p2.participant_id
            WHERE p1.user_id <> p2.user_id
        )
        SELECT
            COUNT(1) FILTER (WHERE (user1_id = u.comparable_user_id AND result = 'WHITE_WIN') OR
                                   (user2_id = u.comparable_user_id AND result = 'BLACK_WIN')) AS user1_wins,
            COUNT(1) FILTER (WHERE (user1_id = u.opponent_user_id AND result = 'WHITE_WIN') OR
                                   (user2_id = u.opponent_user_id AND result = 'BLACK_WIN')) AS user2_wins,
            COUNT(1) FILTER (WHERE result = 'DRAW') AS draws
        FROM matches, user_ids AS u
        """, nativeQuery = true)
    List<Integer[]> getStatsAgainstUser(UUID comparableUserId, UUID opponentUserId);
}
