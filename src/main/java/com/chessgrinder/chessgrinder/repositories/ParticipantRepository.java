package com.chessgrinder.chessgrinder.repositories;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.*;
import org.springframework.data.rest.core.annotation.*;

@RepositoryRestResource
public interface ParticipantRepository extends PagingAndSortingRepository<Participant, UUID>, CrudRepository<Participant, UUID> {

    @Query("SELECT p FROM Participant p WHERE p.tournament.id = :tournamentId")
    List<Participant> findByTournamentId(UUID tournamentId);
}
