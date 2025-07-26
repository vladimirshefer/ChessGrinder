package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.TournamentEventScheduleEntity;
import com.chessgrinder.chessgrinder.enums.TournamentEventScheduleStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

public interface TournamentEventScheduleRepository extends PagingAndSortingRepository<TournamentEventScheduleEntity, UUID>, ListCrudRepository<TournamentEventScheduleEntity, UUID> {

    /**
     * Find all tournament event schedules.
     *
     * @return A list of all tournament event schedules
     */
    @Query("SELECT s FROM TournamentEventScheduleEntity s")
    List<TournamentEventScheduleEntity> findAll();

    /**
     * Find all tournament event schedules with the given status.
     *
     * @param status The status to filter by
     * @return A list of tournament event schedules with the given status
     */
    @Query("SELECT s FROM TournamentEventScheduleEntity s WHERE s.status = :status")
    List<TournamentEventScheduleEntity> findAllByStatus(TournamentEventScheduleStatus status);

    /**
     * Find all tournament event schedules for a specific day of the week.
     *
     * @param dayOfWeek The day of the week to filter by
     * @return A list of tournament event schedules for the given day of the week
     */
    @Query("SELECT s FROM TournamentEventScheduleEntity s WHERE s.dayOfWeek = :dayOfWeek")
    List<TournamentEventScheduleEntity> findAllByDayOfWeek(DayOfWeek dayOfWeek);

    /**
     * Find all active tournament event schedules for a specific day of the week.
     *
     * @param dayOfWeek The day of the week to filter by
     * @return A list of active tournament event schedules for the given day of the week
     */
    @Query("SELECT s FROM TournamentEventScheduleEntity s WHERE s.dayOfWeek = :dayOfWeek AND s.status = 'ACTIVE'")
    List<TournamentEventScheduleEntity> findAllActiveByDayOfWeek(DayOfWeek dayOfWeek);
}