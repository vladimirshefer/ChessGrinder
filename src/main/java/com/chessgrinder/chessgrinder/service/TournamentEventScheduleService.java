package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.TournamentEventScheduleDto;
import com.chessgrinder.chessgrinder.entities.TournamentEventEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEventScheduleEntity;
import com.chessgrinder.chessgrinder.enums.TournamentEventScheduleStatus;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.mappers.TournamentEventScheduleMapper;
import com.chessgrinder.chessgrinder.repositories.TournamentEventRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentEventScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing tournament event schedules.
 * A tournament event schedule represents a recurring tournament event that happens on a specific day of the week.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TournamentEventScheduleService {

    private final TournamentEventScheduleRepository scheduleRepository;
    private final TournamentEventRepository eventRepository;
    private final TournamentEventScheduleMapper scheduleMapper;
    private final TournamentEventService eventService;

    /**
     * Find all tournament event schedules.
     *
     * @return A list of all tournament event schedules
     */
    @Transactional(readOnly = true)
    public List<TournamentEventScheduleDto> findAllSchedules() {
        return scheduleMapper.toDto(scheduleRepository.findAll());
    }

    /**
     * Find all tournament event schedules with the given status.
     *
     * @param status The status to filter by
     * @return A list of tournament event schedules with the given status
     */
    @Transactional(readOnly = true)
    public List<TournamentEventScheduleDto> findSchedulesByStatus(TournamentEventScheduleStatus status) {
        return scheduleMapper.toDto(scheduleRepository.findAllByStatus(status));
    }

    /**
     * Find a tournament event schedule by ID.
     *
     * @param scheduleId The ID of the schedule to find
     * @return The tournament event schedule with the given ID
     * @throws ResponseStatusException if the schedule is not found
     */
    @Transactional(readOnly = true)
    public TournamentEventScheduleDto findScheduleById(UUID scheduleId) {
        TournamentEventScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));
        return scheduleMapper.toDto(schedule);
    }

    /**
     * Create a new tournament event schedule.
     *
     * @param name The name of the schedule
     * @param dayOfWeek The day of the week when the tournament event is scheduled
     * @param time The time of day when the tournament event is scheduled
     * @return The created tournament event schedule
     */
    @Transactional
    public TournamentEventScheduleDto createSchedule(String name, DayOfWeek dayOfWeek, LocalTime time) {
        TournamentEventScheduleEntity schedule = TournamentEventScheduleEntity.builder()
                .name(name)
                .dayOfWeek(dayOfWeek)
                .time(time)
                .status(TournamentEventScheduleStatus.ACTIVE)
                .events(new ArrayList<>())
                .build();

        schedule = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(schedule);
    }

    /**
     * Update a tournament event schedule.
     *
     * @param scheduleId The ID of the schedule to update
     * @param name The new name of the schedule
     * @param dayOfWeek The new day of the week when the tournament event is scheduled
     * @param time The new time of day when the tournament event is scheduled
     * @return The updated tournament event schedule
     * @throws ResponseStatusException if the schedule is not found
     */
    @Transactional
    public TournamentEventScheduleDto updateSchedule(UUID scheduleId, String name, DayOfWeek dayOfWeek, LocalTime time) {
        TournamentEventScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        schedule.setName(name);
        schedule.setDayOfWeek(dayOfWeek);
        schedule.setTime(time);

        schedule = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(schedule);
    }

    /**
     * Update the status of a tournament event schedule.
     *
     * @param scheduleId The ID of the schedule to update
     * @param status The new status of the schedule
     * @return The updated tournament event schedule
     * @throws ResponseStatusException if the schedule is not found
     */
    @Transactional
    public TournamentEventScheduleDto updateScheduleStatus(UUID scheduleId, TournamentEventScheduleStatus status) {
        TournamentEventScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        schedule.setStatus(status);

        schedule = scheduleRepository.save(schedule);
        return scheduleMapper.toDto(schedule);
    }

    /**
     * Create a new tournament event based on a schedule.
     *
     * @param scheduleId The ID of the schedule to create an event for
     * @param date The date of the event
     * @param locationName The location name of the event
     * @param locationUrl The location URL of the event
     * @param roundsNumber The number of rounds for the event
     * @param registrationLimit The registration limit for the event
     * @return The created tournament event
     * @throws ResponseStatusException if the schedule is not found or is not active
     */
    @Transactional
    public TournamentEventEntity createEventFromSchedule(
            UUID scheduleId,
            LocalDateTime date,
            String locationName,
            String locationUrl,
            Integer roundsNumber,
            Integer registrationLimit
    ) {
        TournamentEventScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        if (schedule.getStatus() != TournamentEventScheduleStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Schedule is not active");
        }

        TournamentEventEntity event = TournamentEventEntity.builder()
                .name(schedule.getName())
                .date(date)
                .locationName(locationName)
                .locationUrl(locationUrl)
                .status(TournamentStatus.PLANNED)
                .roundsNumber(roundsNumber)
                .registrationLimit(registrationLimit)
                .schedule(schedule)
                .tournaments(new ArrayList<>())
                .build();

        event = eventRepository.save(event);
        return event;
    }

    /**
     * Delete a tournament event schedule.
     *
     * @param scheduleId The ID of the schedule to delete
     * @throws ResponseStatusException if the schedule is not found
     */
    @Transactional
    public void deleteSchedule(UUID scheduleId) {
        TournamentEventScheduleEntity schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schedule not found"));

        // Check if there are any active events associated with this schedule
        for (TournamentEventEntity event : schedule.getEvents()) {
            if (event.getStatus() != TournamentStatus.FINISHED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot delete schedule with active events");
            }
        }

        scheduleRepository.delete(schedule);
    }
}