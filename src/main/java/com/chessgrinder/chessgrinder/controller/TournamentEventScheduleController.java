package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.TournamentEventDto;
import com.chessgrinder.chessgrinder.dto.TournamentEventScheduleDto;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEventEntity;
import com.chessgrinder.chessgrinder.enums.TournamentEventScheduleStatus;
import com.chessgrinder.chessgrinder.service.TournamentEventScheduleService;
import com.chessgrinder.chessgrinder.service.TournamentEventService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tournament-event-schedule")
@RequiredArgsConstructor
public class TournamentEventScheduleController {

    private final TournamentEventScheduleService scheduleService;
    private final TournamentEventService eventService;

    /**
     * Get all tournament event schedules.
     *
     * @return A list of all tournament event schedules
     */
    @GetMapping
    public Map<String, List<TournamentEventScheduleDto>> getAllSchedules() {
        return Map.of("schedules", scheduleService.findAllSchedules());
    }

    /**
     * Get all tournament event schedules with the given status.
     *
     * @param status The status to filter by
     * @return A list of tournament event schedules with the given status
     */
    @GetMapping("/status/{status}")
    public Map<String, List<TournamentEventScheduleDto>> getSchedulesByStatus(@PathVariable TournamentEventScheduleStatus status) {
        return Map.of("schedules", scheduleService.findSchedulesByStatus(status));
    }

    /**
     * Get a tournament event schedule by ID.
     *
     * @param scheduleId The ID of the schedule to get
     * @return The tournament event schedule with the given ID
     */
    @GetMapping("/{scheduleId}")
    public TournamentEventScheduleDto getScheduleById(@PathVariable UUID scheduleId) {
        return scheduleService.findScheduleById(scheduleId);
    }

    /**
     * Create a new tournament event schedule.
     *
     * @param name The name of the schedule
     * @param dayOfWeek The day of the week when the tournament event is scheduled
     * @param time The time of day when the tournament event is scheduled
     * @return The created tournament event schedule
     */
    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping
    public TournamentEventScheduleDto createSchedule(
            @RequestParam String name,
            @RequestParam DayOfWeek dayOfWeek,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time
    ) {
        return scheduleService.createSchedule(name, dayOfWeek, time);
    }

    /**
     * Update a tournament event schedule.
     *
     * @param scheduleId The ID of the schedule to update
     * @param name The new name of the schedule
     * @param dayOfWeek The new day of the week when the tournament event is scheduled
     * @param time The new time of day when the tournament event is scheduled
     * @return The updated tournament event schedule
     */
    @Secured(RoleEntity.Roles.ADMIN)
    @PutMapping("/{scheduleId}")
    public TournamentEventScheduleDto updateSchedule(
            @PathVariable UUID scheduleId,
            @RequestParam String name,
            @RequestParam DayOfWeek dayOfWeek,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time
    ) {
        return scheduleService.updateSchedule(scheduleId, name, dayOfWeek, time);
    }

    /**
     * Update the status of a tournament event schedule.
     *
     * @param scheduleId The ID of the schedule to update
     * @param status The new status of the schedule
     * @return The updated tournament event schedule
     */
    @Secured(RoleEntity.Roles.ADMIN)
    @PutMapping("/{scheduleId}/status")
    public TournamentEventScheduleDto updateScheduleStatus(
            @PathVariable UUID scheduleId,
            @RequestParam TournamentEventScheduleStatus status
    ) {
        return scheduleService.updateScheduleStatus(scheduleId, status);
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
     */
    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping("/{scheduleId}/event")
    public TournamentEventDto createEventFromSchedule(
            @PathVariable UUID scheduleId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            @RequestParam @Nullable String locationName,
            @RequestParam @Nullable String locationUrl,
            @RequestParam Integer roundsNumber,
            @RequestParam @Nullable Integer registrationLimit
    ) {
        TournamentEventEntity event = scheduleService.createEventFromSchedule(
                scheduleId, date, locationName, locationUrl, roundsNumber, registrationLimit
        );
        return eventService.findTournamentEventById(event.getId());
    }

    /**
     * Delete a tournament event schedule.
     *
     * @param scheduleId The ID of the schedule to delete
     */
    @Secured(RoleEntity.Roles.ADMIN)
    @DeleteMapping("/{scheduleId}")
    public void deleteSchedule(@PathVariable UUID scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
    }
}