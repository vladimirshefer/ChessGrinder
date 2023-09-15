package com.chessgrinder.chessgrinder.repositories;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.rest.core.annotation.*;

@RepositoryRestResource
public interface RoundRepository extends PagingAndSortingRepository<Round, UUID>, CrudRepository<Round, UUID> {

    @Query("SELECT r FROM Round r WHERE r.tournament.id = :tournamentId")
    List<Round> findByTournamentId(UUID tournamentId);

    Round findFirstByTournamentIdOrderByNumberDesc(UUID tournamentId);

    @Query("SELECT r FROM Round r WHERE r.isFinished = false AND r.tournament.id = :tournamentId")
    Round findActiveRoundInTournament(UUID tournamentId);
}
