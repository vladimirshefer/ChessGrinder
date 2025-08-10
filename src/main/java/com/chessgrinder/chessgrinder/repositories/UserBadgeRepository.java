package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.UserBadgeEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface UserBadgeRepository extends PagingAndSortingRepository<UserBadgeEntity, UUID>, CrudRepository<UserBadgeEntity, UUID> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM UserBadgeEntity ub WHERE ub.badge.id = :badgeId")
    void deleteAllByBadgeId(UUID badgeId);

}
