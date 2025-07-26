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
