package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface TournamentRepository extends PagingAndSortingRepository<TournamentEntity, UUID>, CrudRepository<TournamentEntity, UUID> {

    @Override
    List<TournamentEntity> findAll();
}
