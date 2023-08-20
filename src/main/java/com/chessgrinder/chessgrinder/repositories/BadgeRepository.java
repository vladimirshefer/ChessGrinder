package com.chessgrinder.chessgrinder.repositories;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.rest.core.annotation.*;

@RepositoryRestResource
public interface BadgeRepository extends PagingAndSortingRepository<Badge, UUID>, CrudRepository<Badge, UUID> {

        @Query(value = "SELECT b.*, ub.user_id AS user_badge_user_id, u.id AS user_id FROM badges b LEFT JOIN user_badges ub ON b.id = ub.badge_id LEFT JOIN users u on ub.user_id = u.id WHERE u.id = :userId", nativeQuery = true)
        List<Badge> getAllBadgesByUserId(UUID userId);
}
