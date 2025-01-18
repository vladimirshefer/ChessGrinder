package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface TournamentRepository extends PagingAndSortingRepository<TournamentEntity, UUID>, ListCrudRepository<TournamentEntity, UUID> {
}
