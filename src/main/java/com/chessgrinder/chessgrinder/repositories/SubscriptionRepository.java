package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.SubscriptionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository extends CrudRepository<SubscriptionEntity, UUID> {

    @Query("SELECT s FROM SubscriptionEntity s WHERE s.user.id = :userId")
    List<SubscriptionEntity> findAllByUserId(UUID userId);
}
