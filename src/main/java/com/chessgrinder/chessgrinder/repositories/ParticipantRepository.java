package com.chessgrinder.chessgrinder.repositories;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.rest.core.annotation.*;

@RepositoryRestResource
public interface ParticipantRepository extends PagingAndSortingRepository<ParticipantEntity, UUID>, CrudRepository<ParticipantEntity, UUID> {

    @Query("SELECT p FROM ParticipantEntity p WHERE p.tournamentEntity.id = :tournamentId")
    List<ParticipantEntity> findByTournamentId(UUID tournamentId);

    @Query("SELECT p FROM ParticipantEntity p WHERE p.tournamentEntity.id = :tournamentId AND p.userEntity.id = :userId")
    ParticipantEntity findByTournamentIdAndUserId(UUID tournamentId, UUID userId);

}
