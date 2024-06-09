package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.SubscriptionLevelEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SubscriptionLevelRepository extends CrudRepository<SubscriptionLevelEntity, UUID> {
}
