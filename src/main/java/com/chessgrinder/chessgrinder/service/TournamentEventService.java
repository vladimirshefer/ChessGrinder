package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.TournamentEventDto;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.mappers.TournamentEventMapper;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentEventRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing tournament events.
 * A tournament event can contain multiple tournaments (brackets).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TournamentEventService {

    private final TournamentEventRepository tournamentEventRepository;
    private final TournamentRepository tournamentRepository;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final TournamentEventMapper tournamentEventMapper;
    private final TournamentService tournamentService;

    /**
     * Find all tournament events.
     *
     * @return A list of all tournament events
     */
    @Transactional(readOnly = true)
    public List<TournamentEventDto> findTournamentEvents() {
        return tournamentEventMapper.toDto(tournamentEventRepository.findAll());
    }

    public List<TournamentEventDto> findTournamentEventsByStatus(TournamentStatus status) {
        return tournamentEventMapper.toDto(tournamentEventRepository.findAllByStatus(status));
    }

    /**
     * Find a tournament event by ID.
     *
     * @param eventId The ID of the event to find
     * @return The tournament event with the given ID
     * @throws ResponseStatusException if the event is not found
     */
    @Transactional(readOnly = true)
    public TournamentEventDto findTournamentEventById(UUID eventId) {
        TournamentEventEntity event = tournamentEventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        return tournamentEventMapper.toDto(event);
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
     * @throws ResponseStatusException if the event is not found
     */
    @Transactional
    public TournamentEventDto updateTournamentEvent(
            UUID eventId,
            String name,
            LocalDateTime date,
            String locationName,
            String locationUrl,
            Integer roundsNumber,
            Integer registrationLimit
    ) {
        TournamentEventEntity event = tournamentEventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        event.setName(name);
        event.setDate(date);
        event.setLocationName(locationName);
        event.setLocationUrl(locationUrl);
        event.setRoundsNumber(roundsNumber);
        event.setRegistrationLimit(registrationLimit);

        event = tournamentEventRepository.save(event);
        return tournamentEventMapper.toDto(event);
    }

    public TournamentEventEntity createTournamentEvent(
            String name,
            LocalDateTime date,
            String locationName,
            String locationUrl,
            Integer roundsNumber,
            Integer registrationLimit
    ) {
        TournamentEventEntity event = TournamentEventEntity.builder()
                .name(name)
                .date(date)
                .locationName(locationName)
                .locationUrl(locationUrl)
                .status(TournamentStatus.PLANNED)
                .roundsNumber(roundsNumber)
                .registrationLimit(registrationLimit)
                .tournaments(new ArrayList<>())
                .build();

        return tournamentEventRepository.save(event);
    }

    @Transactional
    public ParticipantEntity registerParticipant(
            UUID eventId,
            UserEntity user,
            String nickname,
            int initialEloPoints
    ) {
        TournamentEventEntity event = tournamentEventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        // Check if the participant is already registered for this event
        List<ParticipantEntity> existingParticipants = participantRepository.findByEventId(eventId);
        for (ParticipantEntity existingParticipant : existingParticipants) {
            if (existingParticipant.getUser().getId().equals(user.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already registered for this event");
            }
        }

        // Check if the event has reached its registration limit
        if (event.getRegistrationLimit() != null && existingParticipants.size() >= event.getRegistrationLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event registration limit reached");
        }

        ParticipantEntity participant = ParticipantEntity.builder()
                .user(user)
                .nickname(nickname)
                .initialEloPoints(initialEloPoints)
                .finalEloPoints(0)
                .score(java.math.BigDecimal.ZERO)
                .buchholz(java.math.BigDecimal.ZERO)
                .isMissing(false)
                .isModerator(false)
                .place(0)
                .build();

        return participantRepository.save(participant);
    }

    /**
     * Start a tournament event by creating tournaments and distributing participants.
     *
     * @param eventId The ID of the event to start
     * @param numTournaments The number of tournaments to create
     * @param ratingThreshold The rating threshold for distributing participants
     */
    @Transactional
    public void startEvent(UUID eventId, int numTournaments, int ratingThreshold) {
        TournamentEventEntity event = tournamentEventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (event.getStatus() != TournamentStatus.PLANNED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event is not in PLANNED status");
        }

        // Get all participants registered for this event
        List<ParticipantEntity> participants = participantRepository.findByEventId(eventId);

        // Create tournaments
        List<TournamentEntity> tournaments = new ArrayList<>();
        for (int i = 0; i < numTournaments; i++) {
            String tournamentName = event.getName() + " - Tournament " + (i + 1);
            if (numTournaments == 2) {
                tournamentName = event.getName() + (i == 0 ? " - Chill League" : " - TryHard League");
            }

            TournamentEntity tournament = TournamentEntity.builder()
                    .name(tournamentName)
                    .locationName(event.getLocationName())
                    .locationUrl(event.getLocationUrl())
                    .date(event.getDate())
                    .status(TournamentStatus.ACTIVE)
                    .roundsNumber(event.getRoundsNumber())
                    .registrationLimit(null)
                    .hasEloCalculated(false)
                    .event(event)
                    .rounds(new ArrayList<>())
                    .build();

            tournaments.add(tournamentRepository.save(tournament));
        }

        // Distribute participants across tournaments
        if (numTournaments == 1) {
            // If there's only one tournament, assign all participants to it
            for (ParticipantEntity participant : participants) {
                participant.setTournament(tournaments.get(0));
                participantRepository.save(participant);
            }
        } else if (numTournaments == 2) {
            // If there are two tournaments, distribute based on rating
            for (ParticipantEntity participant : participants) {
                if (participant.getInitialEloPoints() < ratingThreshold) {
                    participant.setTournament(tournaments.get(0)); // Chill League
                } else {
                    participant.setTournament(tournaments.get(1)); // TryHard League
                }
                participantRepository.save(participant);
            }
        } else {
            // For more than 2 tournaments, distribute evenly
            for (int i = 0; i < participants.size(); i++) {
                ParticipantEntity participant = participants.get(i);
                participant.setTournament(tournaments.get(i % numTournaments));
                participantRepository.save(participant);
            }
        }

        // Update event status
        event.setStatus(TournamentStatus.ACTIVE);
        tournamentEventRepository.save(event);
    }

    @Transactional
    public void finishEvent(UUID eventId) {
        TournamentEventEntity event = tournamentEventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (event.getStatus() != TournamentStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event is not in ACTIVE status");
        }

        // Finish all tournaments in the event
        for (TournamentEntity tournament : event.getTournaments()) {
            tournamentService.finishTournament(tournament.getId());
        }

        event.setStatus(TournamentStatus.FINISHED);
        tournamentEventRepository.save(event);
    }

    public void deleteEvent(UUID eventId) {
        tournamentEventRepository.deleteById(eventId);
    }
}
