package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.SubscriptionEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface SubscriptionRepository extends CrudRepository<SubscriptionEntity, UUID> {
}
