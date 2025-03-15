package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private TournamentRepository tournamentRepository;

    @Test
    void testStatsVsUser() {
        UUID comparableUserId = UUID.randomUUID();
        UUID opponentUserId = UUID.randomUUID();

        UserEntity comparableUser = userRepository.save(createUser(comparableUserId, "comparable"));
        UserEntity opponentUser = userRepository.save(createUser(opponentUserId, "opponent"));
        TournamentEntity tournamentEntity = tournamentRepository.save(createTournament());
        ParticipantEntity participant1 = participantRepository.save(createParticipant(comparableUser, tournamentEntity));
        ParticipantEntity participant2 = participantRepository.save(createParticipant(opponentUser, tournamentEntity));
        RoundEntity roundEntity = roundRepository.save(createRound(tournamentEntity));
        matchRepository.save(createMatch(participant1, participant2, roundEntity, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(participant1, participant2, roundEntity, MatchResult.DRAW));
        matchRepository.save(createMatch(participant2, participant1, roundEntity, MatchResult.BLACK_WIN));

        // When: Invoke the repository method
        List<Integer[]> stats = userRepository.getStatsAgainstUser(comparableUserId, opponentUserId);

        // Then: Verify the stats result
        assertThat(stats).isNotNull();
        assertThat(stats.size()).isGreaterThan(0);
        assertThat(stats.get(0)).containsExactly(1, 1, 1); // 1 win for each user, 1 draw
    }

    private static MatchEntity createMatch(ParticipantEntity comparableUserParticipant, ParticipantEntity opponentUserParticipant, RoundEntity roundEntity, MatchResult matchResult) {
        return MatchEntity.builder()
                .participant1(comparableUserParticipant)
                .participant2(opponentUserParticipant)
                .result(matchResult)
                .round(roundEntity)
                .build();
    }

    private static RoundEntity createRound(TournamentEntity tournamentEntity) {
        return RoundEntity.builder()
                .tournament(tournamentEntity)
                .isFinished(true)
                .build();
    }

    private static TournamentEntity createTournament() {
        return TournamentEntity.builder()
                .status(TournamentStatus.FINISHED)
                .date(LocalDateTime.now())
                .roundsNumber(6)
                .build();
    }

    private static ParticipantEntity createParticipant(
            UserEntity comparableUser,
            TournamentEntity tournament
    ) {
        return ParticipantEntity.builder()
                .user(comparableUser)
                .nickname(comparableUser.getName())
                .tournament(tournament)
                .buchholz(BigDecimal.ZERO)
                .score(BigDecimal.ZERO)
                .place(1)
                .build();
    }

    private static UserEntity createUser(UUID comparableUserId, String username) {
        return UserEntity.builder()
                .id(comparableUserId)
                .name(username)
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
