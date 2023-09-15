package com.chessgrinder.chessgrinder.repositories;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.rest.core.annotation.*;

@RepositoryRestResource
public interface MatchRepository extends PagingAndSortingRepository<Match, UUID>, CrudRepository<Match, UUID> {

    @Query("SELECT m FROM Match m WHERE m.round.tournament.id = :tournamentId")
    List<Match> findAllByTournamentId(UUID tournamentId);

    Match findByParticipant1AndParticipant2(Participant participant1, Participant participant2);

    @Query("SELECT m FROM Match m WHERE (m.round.tournament.id = :tournamentId AND m.participant1 = :participant1 AND m.participant2 = :participant2) OR (m.round.tournament.id = :tournamentId AND m.participant1 = :participant2 AND m.participant2 = :participant1) ")
    Match findMatchBetweenTwoParticipantsInTournament(UUID tournamentId, Participant participant1, Participant participant2);

}
