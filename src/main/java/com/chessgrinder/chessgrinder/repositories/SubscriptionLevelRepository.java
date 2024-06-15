package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.SubscriptionLevelEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionLevelRepository extends CrudRepository<SubscriptionLevelEntity, UUID> {

    Optional<SubscriptionLevelEntity> findByName(String name);

    /** Returns entity with checking its existence */
    default SubscriptionLevelEntity getByName(String name) {
        return findByName(name)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No sunscription level with name " + name
                ));
    }
}
