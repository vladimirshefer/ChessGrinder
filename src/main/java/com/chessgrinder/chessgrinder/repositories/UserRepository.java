package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends PagingAndSortingRepository<UserEntity, UUID>, CrudRepository<UserEntity, UUID> {

    @Override
    List<UserEntity> findAll();

    @Nullable
    UserEntity findByUsername(String userName);
    @Nullable
    UserEntity findByEmail(String email);

    @Modifying
    @Query("UPDATE UserEntity u SET u.reputation = u.reputation + :amount WHERE u.id = :userId")
    void addReputation(UUID userId, Integer amount);
}
