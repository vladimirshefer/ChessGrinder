package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface MatchRepository extends PagingAndSortingRepository<MatchEntity, UUID>, CrudRepository<MatchEntity, UUID> {

    @Query("SELECT m FROM MatchEntity m WHERE m.round.tournament.id = :tournamentId")
    List<MatchEntity> findAllByTournamentId(UUID tournamentId);

    MatchEntity findByParticipant1AndParticipant2(ParticipantEntity participant1, ParticipantEntity participant2);

    @Query("SELECT m FROM MatchEntity m WHERE (m.round.tournament.id = :tournamentId AND m.participant1 = :participant1 AND m.participant2 = :participant2) OR (m.round.tournament.id = :tournamentId AND m.participant1 = :participant2 AND m.participant2 = :participant1) ")
    MatchEntity findMatchBetweenTwoParticipantsInTournament(UUID tournamentId, ParticipantEntity participant1, ParticipantEntity participant2);

}
