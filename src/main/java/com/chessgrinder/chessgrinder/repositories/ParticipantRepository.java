package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipantRepository extends PagingAndSortingRepository<ParticipantEntity, UUID>, CrudRepository<ParticipantEntity, UUID> {

    @Query("SELECT p FROM ParticipantEntity p WHERE p.tournament.id = :tournamentId")
    List<ParticipantEntity> findByTournamentId(UUID tournamentId);

    @Query("SELECT p FROM ParticipantEntity p WHERE p.tournament.id = :tournamentId AND p.user.id = :userId")
    ParticipantEntity findByTournamentIdAndUserId(UUID tournamentId, UUID userId);

    @Query("SELECT p FROM ParticipantEntity p WHERE p.user.id = :userId ORDER BY p.tournament.date DESC")
    List<ParticipantEntity> findAllByUserId(UUID userId);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE ParticipantEntity p SET p.initialEloPoints = 0, p.finalEloPoints = 0")
    void clearAllEloPoints();

    @Query("SELECT p FROM ParticipantEntity p WHERE p.tournament.id = :tournamentId AND p.place = 1")
    Optional<ParticipantEntity> findFirstPlaceByTournamentId(UUID tournamentId);
    
    @Query("SELECT COUNT(p) FROM ParticipantEntity p WHERE p.tournament.id = :tournamentId")
    long countByTournament(UUID tournamentId);

}
