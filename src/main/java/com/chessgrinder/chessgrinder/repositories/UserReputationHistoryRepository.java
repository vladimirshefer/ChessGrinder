package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.UserReputationHistoryEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface UserReputationHistoryRepository extends ListCrudRepository<UserReputationHistoryEntity, UUID> {
}
