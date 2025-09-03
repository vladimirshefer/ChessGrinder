package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.MatchEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface MatchRepository extends PagingAndSortingRepository<MatchEntity, UUID>, ListCrudRepository<MatchEntity, UUID> {

    List<MatchEntity> findMatchEntitiesByRoundId(UUID roundId);

    @Query("SELECT m from MatchEntity m WHERE m.round.tournament.id = :tournamentId and m.round.isFinished=true")
    List<MatchEntity> findFinishedByTournamentId(UUID tournamentId);

}
