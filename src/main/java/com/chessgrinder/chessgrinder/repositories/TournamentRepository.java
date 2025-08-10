package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface TournamentRepository extends PagingAndSortingRepository<TournamentEntity, UUID>, ListCrudRepository<TournamentEntity, UUID> {

    @Modifying(flushAutomatically = true)
    @Query("UPDATE TournamentEntity t SET t.hasEloCalculated = false")
    void clearAllEloPoints();

    @Query("SELECT t FROM TournamentEntity t WHERE t.status = :status")
    List<TournamentEntity> findAllByStatus(TournamentStatus status);

}
