package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

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
}
