package com.chessgrinder.chessgrinder.repositories;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.rest.core.annotation.*;

@RepositoryRestResource
public interface MatchRepository extends PagingAndSortingRepository<MatchEntity, UUID>, CrudRepository<MatchEntity, UUID> {

    @Query("SELECT m FROM MatchEntity m WHERE m.roundEntity.tournamentEntity.id = :tournamentId")
    List<MatchEntity> findAllByTournamentId(UUID tournamentId);

    MatchEntity findByParticipantEntity1AndParticipantEntity2(ParticipantEntity participantEntity1, ParticipantEntity participantEntity2);

    @Query("SELECT m FROM MatchEntity m WHERE (m.roundEntity.tournamentEntity.id = :tournamentId AND m.participantEntity1 = :participantEntity1 AND m.participantEntity2 = :participantEntity2) OR (m.roundEntity.tournamentEntity.id = :tournamentId AND m.participantEntity1 = :participantEntity2 AND m.participantEntity2 = :participantEntity1) ")
    MatchEntity findMatchBetweenTwoParticipantsInTournament(UUID tournamentId, ParticipantEntity participantEntity1, ParticipantEntity participantEntity2);

}
