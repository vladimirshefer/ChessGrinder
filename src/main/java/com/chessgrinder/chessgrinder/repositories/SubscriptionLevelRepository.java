package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.SubscriptionLevelEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionLevelRepository extends CrudRepository<SubscriptionLevelEntity, UUID> {

    Optional<SubscriptionLevelEntity> findByName(String name);
}
