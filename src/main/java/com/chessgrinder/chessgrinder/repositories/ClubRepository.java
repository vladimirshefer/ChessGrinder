package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.ClubEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ClubRepository extends CrudRepository<ClubEntity, UUID> {

    @Override
    List<ClubEntity> findAll();
}
