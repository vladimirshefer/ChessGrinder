package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.ListDto;
import com.chessgrinder.chessgrinder.dto.TournamentEventDto;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEventEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.AuthenticatedUserArgumentResolver;
import com.chessgrinder.chessgrinder.security.AuthenticatedUserArgumentResolver.AuthenticatedUser;
import com.chessgrinder.chessgrinder.service.TournamentEventService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tournament-event")
@RequiredArgsConstructor
public class TournamentEventController {

    private final TournamentEventService tournamentEventService;
    private final UserRepository userRepository;

    /**
     * Get all tournament events.
     *
     * @return A list of all tournament events
     */
    @GetMapping
    public ListDto<TournamentEventDto> getTournamentEvents() {
        return ListDto.<TournamentEventDto>builder().values(tournamentEventService.findTournamentEvents()).build();
    }

    /**
     * Get all tournament events with the given status.
     *
     * @param status The status to filter by
     * @return A list of tournament events with the given status
     */
    @GetMapping("/status/{status}")
    public Map<String, List<TournamentEventDto>> getTournamentEventsByStatus(@PathVariable TournamentStatus status) {
        return Map.of("events", tournamentEventService.findTournamentEventsByStatus(status));
    }

    /**
     * Get a tournament event by ID.
     *
     * @param eventId The ID of the event to get
     * @return The tournament event with the given ID
     */
    @GetMapping("/{eventId}")
    public TournamentEventDto getTournamentEvent(@PathVariable UUID eventId) {
        return tournamentEventService.findTournamentEventById(eventId);
    }

    /**
     * Create a new tournament event.
     *
     * @param name The name of the event
     * @param date The date of the event
     * @param locationName The location name of the event
     * @param locationUrl The location URL of the event
     * @param roundsNumber The number of rounds for the event
     * @param registrationLimit The registration limit for the event
     * @return The created tournament event
     */
    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping
    public TournamentEventDto createTournamentEvent(
            @RequestParam String name,
            @RequestParam LocalDateTime date,
            @RequestParam @Nullable String locationName,
            @RequestParam @Nullable String locationUrl,
            @RequestParam Integer roundsNumber,
            @RequestParam @Nullable Integer registrationLimit
    ) {
        TournamentEventEntity event = tournamentEventService.createTournamentEvent(
                name, date, locationName, locationUrl, roundsNumber, registrationLimit
        );
        return tournamentEventService.findTournamentEventById(event.getId());
    }

    /**
     * Update a tournament event.
     *
     * @param eventId The ID of the event to update
     * @param name The new name of the event
     * @param date The new date of the event
     * @param locationName The new location name of the event
     * @param locationUrl The new location URL of the event
     * @param roundsNumber The new number of rounds for the event
     * @param registrationLimit The new registration limit for the event
     * @return The updated tournament event
     */
    @Secured(RoleEntity.Roles.ADMIN)
    @PutMapping("/{eventId}")
    public TournamentEventDto updateTournamentEvent(
            @PathVariable UUID eventId,
            @RequestParam String name,
            @RequestParam LocalDateTime date,
            @RequestParam @Nullable String locationName,
            @RequestParam @Nullable String locationUrl,
            @RequestParam Integer roundsNumber,
            @RequestParam @Nullable Integer registrationLimit
    ) {
        return tournamentEventService.updateTournamentEvent(
                eventId, name, date, locationName, locationUrl, roundsNumber, registrationLimit
        );
    }

    /**
     * Delete a tournament event.
     *
     * @param eventId The ID of the event to delete
     */
    @Secured(RoleEntity.Roles.ADMIN)
    @DeleteMapping("/{eventId}")
    public void deleteTournamentEvent(@PathVariable UUID eventId) {
        tournamentEventService.deleteEvent(eventId);
    }

    /**
     * Start a tournament event.
     *
     * @param eventId The ID of the event to start
     * @param numTournaments The number of tournaments to create
     * @param ratingThreshold The rating threshold for distributing participants
     */
    @PreAuthorize("hasPermission(#eventId,'TournamentEventEntity','MODERATOR')")
    @PostMapping("/{eventId}/action/start")
    public void startTournamentEvent(
            @PathVariable UUID eventId,
            @RequestParam int numTournaments,
            @RequestParam int ratingThreshold
    ) {
        tournamentEventService.startEvent(eventId, numTournaments, ratingThreshold);
    }

    /**
     * Finish a tournament event.
     *
     * @param eventId The ID of the event to finish
     */
    @PreAuthorize("hasPermission(#eventId,'TournamentEventEntity','MODERATOR')")
    @PostMapping("/{eventId}/action/finish")
    public void finishTournamentEvent(@PathVariable UUID eventId) {
        tournamentEventService.finishEvent(eventId);
    }

    @PostMapping("/{eventId}/action/participate")
    public void registerParticipant(
            @AuthenticatedUser
            UserEntity authenticatedUser,
            @PathVariable
            UUID eventId,
            @RequestParam
            @Nullable
            String nickname,
            Authentication authentication
    ) {
        if (!authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        ParticipantEntity participant = tournamentEventService.registerParticipant(
                eventId,
                authenticatedUser,
                nickname != null ? nickname : authenticatedUser.getUsername(),
                authenticatedUser.getEloPoints()
        );
    }
}