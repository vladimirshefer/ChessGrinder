package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.ClubEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

public interface ClubRepository extends CrudRepository<ClubEntity, UUID> {

    @Override
    List<ClubEntity> findAll();

    /**
     * Returns entity with checking its existence
     */
    default ClubEntity getById(UUID id) {
        return findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No club with id " + id));
    }
}
