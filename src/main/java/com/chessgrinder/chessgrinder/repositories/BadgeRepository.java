package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.BadgeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BadgeRepository extends PagingAndSortingRepository<BadgeEntity, UUID>, ListCrudRepository<BadgeEntity, UUID> {

        @Query(value = "SELECT b.*, ub.user_id AS user_badge_user_id, u.id AS user_id FROM badges_table b LEFT JOIN users_badges_table ub ON b.id = ub.badge_id LEFT JOIN users_table u on ub.user_id = u.id WHERE u.id = :userId", nativeQuery = true)
        List<BadgeEntity> getAllBadgesByUserId(UUID userId);

        @Query("SELECT b FROM BadgeEntity b WHERE b.title = :title")
        Optional<BadgeEntity> findByTitle(String title);

}
