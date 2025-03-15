package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UserRepository extends PagingAndSortingRepository<UserEntity, UUID>, ListCrudRepository<UserEntity, UUID> {
    
    @Query("SELECT u FROM UserEntity u ORDER BY u.eloPoints DESC, u.reputation DESC, u.createdAt DESC")
    Page<UserEntity> findAllOrdered(Pageable pageable);

    @Query("SELECT u FROM UserEntity u ORDER BY u.eloPoints DESC, u.reputation DESC, u.createdAt DESC")
    List<UserEntity> findAllOrdered();

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

}
