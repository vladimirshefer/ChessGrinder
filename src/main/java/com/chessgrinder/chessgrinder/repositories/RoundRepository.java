package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.RoundEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface RoundRepository extends PagingAndSortingRepository<RoundEntity, UUID>, CrudRepository<RoundEntity, UUID> {

    @Query("SELECT r FROM RoundEntity r WHERE r.tournament.id = :tournamentId")
    List<RoundEntity> findByTournamentId(UUID tournamentId);

    RoundEntity findFirstByTournamentIdOrderByNumberDesc(UUID tournamentId);

    @Query("SELECT r FROM RoundEntity r WHERE r.isFinished = false AND r.tournament.id = :tournamentId")
    RoundEntity findActiveRoundInTournament(UUID tournamentId);

    @Query("SELECT r FROM RoundEntity r WHERE r.tournament.id = :tournamentId and r.number = :roundNumber")
    RoundEntity findByTournamentIdAndNumber(UUID tournamentId, Integer roundNumber);

}
