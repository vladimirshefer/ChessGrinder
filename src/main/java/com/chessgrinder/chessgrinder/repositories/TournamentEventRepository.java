package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.TournamentEventEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface TournamentEventRepository extends PagingAndSortingRepository<TournamentEventEntity, UUID>, ListCrudRepository<TournamentEventEntity, UUID> {

    /**
     * Find all tournament events.
     *
     * @return A list of all tournament events
     */
    @Query("SELECT e FROM TournamentEventEntity e")
    List<TournamentEventEntity> findAll();

    /**
     * Find all tournament events with the given status.
     *
     * @param status The status to filter by
     * @return A list of tournament events with the given status
     */
    @Query("SELECT e FROM TournamentEventEntity e WHERE e.status = :status")
    List<TournamentEventEntity> findAllByStatus(TournamentStatus status);

    /**
     * Find all tournament events with the given status, ordered by date.
     *
     * @param status The status to filter by
     * @return A list of tournament events with the given status, ordered by date
     */
    @Query("SELECT e FROM TournamentEventEntity e WHERE e.status = :status ORDER BY e.date DESC")
    List<TournamentEventEntity> findAllByStatusOrderByDateDesc(TournamentStatus status);
}
