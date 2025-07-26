package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class TournamentRepositoryTest {

    @Autowired
    private TournamentRepository tournamentRepository;

    @AfterEach
    void tearDown() {
        tournamentRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        tournamentRepository.deleteAll();
    }

    @Test
    void testClearAllEloPoints() {
        tournamentRepository.save(createTournament(TournamentStatus.FINISHED, true));
        tournamentRepository.save(createTournament(TournamentStatus.FINISHED, true));
        tournamentRepository.save(createTournament(TournamentStatus.ACTIVE, true));

        tournamentRepository.clearAllEloPoints();

        List<TournamentEntity> allTournaments = tournamentRepository.findAll();

        assertEquals(3, allTournaments.size());
        assertEquals(List.of(false, false, false), allTournaments.stream().map(TournamentEntity::isHasEloCalculated).toList());
    }

    @Test
    void testFindAllByStatus() {
        // Given: Create tournaments with different statuses
        TournamentEntity finishedTournament1 = createTournament(TournamentStatus.FINISHED, false);
        TournamentEntity finishedTournament2 = createTournament(TournamentStatus.FINISHED, false);
        TournamentEntity activeTournament = createTournament(TournamentStatus.ACTIVE, false);
        TournamentEntity plannedTournament = createTournament(TournamentStatus.PLANNED, false);

        tournamentRepository.save(finishedTournament1);
        tournamentRepository.save(finishedTournament2);
        tournamentRepository.save(activeTournament);
        tournamentRepository.save(plannedTournament);

        // When: Call findAllByStatus with FINISHED status
        List<TournamentEntity> finishedTournaments = tournamentRepository.findAllByStatus(TournamentStatus.FINISHED);

        // Then: Verify only FINISHED tournaments are returned
        assertThat(finishedTournaments).hasSize(2);
        assertThat(finishedTournaments).allMatch(tournament -> tournament.getStatus() == TournamentStatus.FINISHED);

        // When: Call findAllByStatus with ACTIVE status
        List<TournamentEntity> activeTournaments = tournamentRepository.findAllByStatus(TournamentStatus.ACTIVE);

        // Then: Verify only ACTIVE tournaments are returned
        assertThat(activeTournaments).hasSize(1);
        assertThat(activeTournaments).allMatch(tournament -> tournament.getStatus() == TournamentStatus.ACTIVE);

        // When: Call findAllByStatus with PLANNED status
        List<TournamentEntity> plannedTournaments = tournamentRepository.findAllByStatus(TournamentStatus.PLANNED);

        // Then: Verify only PLANNED tournaments are returned
        assertThat(plannedTournaments).hasSize(1);
        assertThat(plannedTournaments).allMatch(tournament -> tournament.getStatus() == TournamentStatus.PLANNED);
    }

    private TournamentEntity createTournament(TournamentStatus status, boolean hasEloCalculated) {
        return TournamentEntity.builder()
                .status(status)
                .date(LocalDateTime.now())
                .roundsNumber(6)
                .hasEloCalculated(hasEloCalculated)
                .build();
    }

    @TestConfiguration
    static class AuditorAwareTestConfig {
        @Bean
        public AuditorAware<String> auditorProvider() {
            return () -> Optional.of("test_user");
        }
    }
}
