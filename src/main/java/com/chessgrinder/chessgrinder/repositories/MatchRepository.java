package com.chessgrinder.chessgrinder.repositories;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.rest.core.annotation.*;

@RepositoryRestResource
public interface MatchRepository extends PagingAndSortingRepository<MatchEntity, UUID>, CrudRepository<MatchEntity, UUID> {

    @Query("SELECT m FROM MatchEntity m WHERE m.round.tournament.id = :tournamentId")
    List<MatchEntity> findAllByTournamentId(UUID tournamentId);

    MatchEntity findByParticipant1AndParticipant2(ParticipantEntity participant1, ParticipantEntity participant2);

    @Query("SELECT m FROM MatchEntity m WHERE (m.round.tournament.id = :tournamentId AND m.participant1 = :participant1 AND m.participant2 = :participant2) OR (m.round.tournament.id = :tournamentId AND m.participant1 = :participant2 AND m.participant2 = :participant1) ")
    MatchEntity findMatchBetweenTwoParticipantsInTournament(UUID tournamentId, ParticipantEntity participant1, ParticipantEntity participant2);

}
