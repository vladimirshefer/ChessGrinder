package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface ParticipantRepository extends PagingAndSortingRepository<ParticipantEntity, UUID>, CrudRepository<ParticipantEntity, UUID> {

    @Query("SELECT p FROM ParticipantEntity p WHERE p.tournament.id = :tournamentId")
    List<ParticipantEntity> findByTournamentId(UUID tournamentId);

    @Query("SELECT p FROM ParticipantEntity p WHERE p.tournament.id = :tournamentId AND p.user.id = :userId")
    ParticipantEntity findByTournamentIdAndUserId(UUID tournamentId, UUID userId);

}
