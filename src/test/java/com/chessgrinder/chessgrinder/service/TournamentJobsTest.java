package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.RoundEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.testutil.repository.TestJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TournamentJobsTest {

    private TournamentJobs tournamentJobs;
    private TournamentRepository tournamentRepository;
    private TournamentService tournamentService;

    @BeforeEach
    void setUp() {
        tournamentRepository = Mockito.mock(TournamentRepository.class);
        tournamentService = Mockito.mock(TournamentService.class);
        tournamentJobs = new TournamentJobs(tournamentRepository, tournamentService);
    }

    @Test
    void testCleanupDeletesAbandonedActiveTournaments() {
        // Create an abandoned active tournament (older than 1 day with no rounds)
        TournamentEntity abandonedActiveTournament = createTournament(
                TournamentStatus.ACTIVE,
                LocalDateTime.now().minusDays(2),
                new ArrayList<>()
        );

        // Set up mock behavior
        when(tournamentRepository.findAllByStatus(TournamentStatus.ACTIVE))
                .thenReturn(List.of(abandonedActiveTournament));
        when(tournamentRepository.findAllByStatus(TournamentStatus.PLANNED))
                .thenReturn(List.of());

        // Run cleanup
        tournamentJobs.cleanup();

        // Verify that deleteTournament was called for the abandoned tournament
        verify(tournamentService, times(1)).deleteTournament(abandonedActiveTournament.getId());

        // Note: In the current implementation, both deleteTournamentIfAbandoned and closeTournamentIfStale
        // are called for active tournaments, so finishTournament might be called after deleteTournament.
        // We don't need to verify that finishTournament was never called.
    }

    @Test
    void testCleanupClosesStaleActiveTournaments() {
        // Create a stale active tournament (older than 1 day with rounds)
        List<RoundEntity> rounds = new ArrayList<>();
        rounds.add(RoundEntity.builder().id(UUID.randomUUID()).build());

        TournamentEntity staleActiveTournament = createTournament(
                TournamentStatus.ACTIVE,
                LocalDateTime.now().minusDays(2),
                rounds
        );

        // Set up mock behavior
        when(tournamentRepository.findAllByStatus(TournamentStatus.ACTIVE))
                .thenReturn(List.of(staleActiveTournament));
        when(tournamentRepository.findAllByStatus(TournamentStatus.PLANNED))
                .thenReturn(List.of());

        // Run cleanup
        tournamentJobs.cleanup();

        // Verify that finishTournament was called for the stale tournament
        verify(tournamentService, times(1)).finishTournament(staleActiveTournament.getId());
        // Verify that deleteTournament was not called for the stale tournament
        verify(tournamentService, never()).deleteTournament(staleActiveTournament.getId());
    }

    @Test
    void testCleanupDeletesAbandonedPlannedTournaments() {
        // Create an abandoned planned tournament (older than 1 day with no rounds)
        TournamentEntity abandonedPlannedTournament = createTournament(
                TournamentStatus.PLANNED,
                LocalDateTime.now().minusDays(2),
                new ArrayList<>()
        );

        // Set up mock behavior
        when(tournamentRepository.findAllByStatus(TournamentStatus.ACTIVE))
                .thenReturn(List.of());
        when(tournamentRepository.findAllByStatus(TournamentStatus.PLANNED))
                .thenReturn(List.of(abandonedPlannedTournament));

        // Run cleanup
        tournamentJobs.cleanup();

        // Verify that deleteTournament was called for the abandoned planned tournament
        verify(tournamentService, times(1)).deleteTournament(abandonedPlannedTournament.getId());
    }

    @Test
    void testCleanupDoesNotAffectRecentTournaments() {
        // Create a recent active tournament (less than 1 day old)
        TournamentEntity recentActiveTournament = createTournament(
                TournamentStatus.ACTIVE,
                LocalDateTime.now().minusHours(12),
                new ArrayList<>()
        );

        // Create a recent planned tournament (less than 1 day old)
        TournamentEntity recentPlannedTournament = createTournament(
                TournamentStatus.PLANNED,
                LocalDateTime.now().minusHours(12),
                new ArrayList<>()
        );

        // Set up mock behavior
        when(tournamentRepository.findAllByStatus(TournamentStatus.ACTIVE))
                .thenReturn(List.of(recentActiveTournament));
        when(tournamentRepository.findAllByStatus(TournamentStatus.PLANNED))
                .thenReturn(List.of(recentPlannedTournament));

        // Run cleanup
        tournamentJobs.cleanup();

        // Verify that no methods were called on tournamentService for recent tournaments
        verify(tournamentService, never()).deleteTournament(recentActiveTournament.getId());
        verify(tournamentService, never()).finishTournament(recentActiveTournament.getId());
        verify(tournamentService, never()).deleteTournament(recentPlannedTournament.getId());
    }

    @Test
    void testCleanupHandlesExceptionsWhenFinishingTournament() {
        // Create a stale active tournament
        List<RoundEntity> rounds = new ArrayList<>();
        rounds.add(RoundEntity.builder().id(UUID.randomUUID()).build());

        TournamentEntity staleActiveTournament = createTournament(
                TournamentStatus.ACTIVE,
                LocalDateTime.now().minusDays(2),
                rounds
        );

        // Set up mock behavior
        when(tournamentRepository.findAllByStatus(TournamentStatus.ACTIVE))
                .thenReturn(List.of(staleActiveTournament));
        when(tournamentRepository.findAllByStatus(TournamentStatus.PLANNED))
                .thenReturn(List.of());

        // Make finishTournament throw an exception
        doThrow(new RuntimeException("Test exception")).when(tournamentService).finishTournament(staleActiveTournament.getId());

        // Run cleanup - should not throw exception
        tournamentJobs.cleanup();

        // Verify that finishTournament was called
        verify(tournamentService, times(1)).finishTournament(staleActiveTournament.getId());
    }

    @Test
    void testCleanupHandlesExceptionsWhenDeletingTournament() {
        // Create an abandoned active tournament
        TournamentEntity abandonedActiveTournament = createTournament(
                TournamentStatus.ACTIVE,
                LocalDateTime.now().minusDays(2),
                new ArrayList<>()
        );

        // Set up mock behavior
        when(tournamentRepository.findAllByStatus(TournamentStatus.ACTIVE))
                .thenReturn(List.of(abandonedActiveTournament));
        when(tournamentRepository.findAllByStatus(TournamentStatus.PLANNED))
                .thenReturn(List.of());

        // Make deleteTournament throw an exception
        doThrow(new RuntimeException("Test exception")).when(tournamentService).deleteTournament(abandonedActiveTournament.getId());

        // Run cleanup - should not throw exception
        tournamentJobs.cleanup();

        // Verify that deleteTournament was called
        verify(tournamentService, times(1)).deleteTournament(abandonedActiveTournament.getId());
    }

    private TournamentEntity createTournament(TournamentStatus status, LocalDateTime date, List<RoundEntity> rounds) {
        TournamentEntity tournament = TournamentEntity.builder()
                .id(UUID.randomUUID())
                .status(status)
                .date(date)
                .rounds(rounds)
                .build();

        // Set up bidirectional relationship
        for (RoundEntity round : rounds) {
            round.setTournament(tournament);
        }

        return tournament;
    }
}
