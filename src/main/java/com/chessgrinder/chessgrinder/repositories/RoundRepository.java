package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.RoundEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RoundRepository extends JpaRepository<RoundEntity, UUID> {

    @Query("SELECT r FROM RoundEntity r WHERE r.tournament.id = :tournamentId")
    List<RoundEntity> findByTournamentId(UUID tournamentId);

    RoundEntity findFirstByTournamentIdOrderByNumberDesc(UUID tournamentId);

    @Query("SELECT r FROM RoundEntity r WHERE r.tournament.id = :tournamentId and r.number = :roundNumber")
    RoundEntity findByTournamentIdAndNumber(UUID tournamentId, Integer roundNumber);

    @Query("SELECT r FROM RoundEntity r WHERE r.tournament.id = :tournamentId and r.number > :roundNumber")
    List<RoundEntity> findAllRoundsWithGreaterRoundNumber(UUID tournamentId, Integer roundNumber);

}
