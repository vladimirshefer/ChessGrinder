package com.chessgrinder.chessgrinder.badges;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class TournamentWinnerBadgeTournamentListenerImplTest {

    private static final String BADGE_TITLE = "Tournament Winner";

    private TournamentWinnerBadgeTournamentListenerImpl listener;

    @Autowired
    private BadgeRepository badgeRepository;
    @Autowired
    private UserBadgeRepository userBadgeRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        listener = new TournamentWinnerBadgeTournamentListenerImpl(
                badgeRepository,
                userBadgeRepository,
                participantRepository
        );
    }

    @Test
    @Transactional
    void tournamentFinishedDoesNotCreateDuplicateWinnerBadge() {
        UserEntity winnerUser = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .username("winner@test")
                .build());
        TournamentEntity tournament = tournamentRepository.save(TournamentEntity.builder()
                .id(UUID.randomUUID())
                .name("Test Tournament")
                .roundsNumber(1)
                .status(TournamentStatus.ACTIVE)
                .build());
        participantRepository.save(ParticipantEntity.builder()
                .id(UUID.randomUUID())
                .user(winnerUser)
                .tournament(tournament)
                .nickname("winner")
                .score(BigDecimal.ONE)
                .buchholz(BigDecimal.ZERO)
                .place(1)
                .build());

        BadgeEntity badge = badgeRepository.save(BadgeEntity.builder()
                .title(BADGE_TITLE)
                .description("Badge given to the winner of a tournament.")
                .build());
        userBadgeRepository.save(UserBadgeEntity.builder()
                .user(winnerUser)
                .badge(badge)
                .build());

        listener.tournamentFinished(tournament);

        assertTrue(userBadgeRepository.existsByUserIdAndBadgeId(winnerUser.getId(), badge.getId()));
    }

    @TestConfiguration
    static class AuditorAwareTestConfig {
        @Bean
        public AuditorAware<String> auditorProvider() {
            return () -> Optional.of("test_user");
        }
    }
}
