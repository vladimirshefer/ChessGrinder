package com.chessgrinder.chessgrinder.repositories;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.rest.core.annotation.*;

@RepositoryRestResource
public interface BadgeRepository extends PagingAndSortingRepository<BadgeEntity, UUID>, CrudRepository<BadgeEntity, UUID> {

        @Query(value = "SELECT b.*, ub.user_id AS user_badge_user_id, u.id AS user_id FROM badges_table b LEFT JOIN users_badges_table ub ON b.id = ub.badge_id LEFT JOIN users_table u on ub.user_id = u.id WHERE u.id = :userId", nativeQuery = true)
        List<BadgeEntity> getAllBadgesByUserId(UUID userId);
}
