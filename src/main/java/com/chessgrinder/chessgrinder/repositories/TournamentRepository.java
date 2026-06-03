package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface TournamentRepository extends JpaRepository<TournamentEntity, UUID> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE TournamentEntity t SET t.hasEloCalculated = false")
    void clearAllEloPoints();

    @Query("SELECT t FROM TournamentEntity t WHERE t.status = :status")
    List<TournamentEntity> findAllByStatus(TournamentStatus status);

    @Query("SELECT t FROM TournamentEntity t WHERE t.owner = :owner AND t.status IN :statuses AND t.createdAt >= :dateAfter")
    List<TournamentEntity> findAllByOwnerAndStatusAndDateAfter(UserEntity owner, List<TournamentStatus> statuses, Instant dateAfter);

}
