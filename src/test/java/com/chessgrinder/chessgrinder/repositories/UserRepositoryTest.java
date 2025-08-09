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
        UserEntity comparableUser = userRepository.save(createUser("comparable"));
        UserEntity opponentUser = userRepository.save(createUser("opponent"));

        // First tournament setup
        TournamentEntity tournamentEntity1 = tournamentRepository.save(createTournament());
        ParticipantEntity participant1Tournament1 = participantRepository.save(createParticipant(comparableUser, tournamentEntity1));
        ParticipantEntity participant2Tournament1 = participantRepository.save(createParticipant(opponentUser, tournamentEntity1));
        RoundEntity roundEntity1Tournament1 = roundRepository.save(createRound(tournamentEntity1));
        matchRepository.save(createMatch(participant1Tournament1, participant2Tournament1, roundEntity1Tournament1, MatchResult.WHITE_WIN));
        RoundEntity roundEntity2Tournament1 = roundRepository.save(createRound(tournamentEntity1));
        matchRepository.save(createMatch(participant1Tournament1, participant2Tournament1, roundEntity2Tournament1, MatchResult.DRAW));
        RoundEntity roundEntity3Tournament1 = roundRepository.save(createRound(tournamentEntity1));
        matchRepository.save(createMatch(participant1Tournament1, participant2Tournament1, roundEntity3Tournament1, MatchResult.BLACK_WIN));

        // Second tournament setup
        TournamentEntity tournamentEntity2 = tournamentRepository.save(createTournament());
        ParticipantEntity participant1Tournament2 = participantRepository.save(createParticipant(comparableUser, tournamentEntity2));
        ParticipantEntity participant2Tournament2 = participantRepository.save(createParticipant(opponentUser, tournamentEntity2));
        RoundEntity roundEntity1Tournament2 = roundRepository.save(createRound(tournamentEntity2));
        matchRepository.save(createMatch(participant1Tournament2, participant2Tournament2, roundEntity1Tournament2, MatchResult.WHITE_WIN));
        RoundEntity roundEntity2Tournament2 = roundRepository.save(createRound(tournamentEntity2));
        matchRepository.save(createMatch(participant2Tournament2, participant1Tournament2, roundEntity2Tournament2, MatchResult.BLACK_WIN));
        RoundEntity roundEntity3Tournament2 = roundRepository.save(createRound(tournamentEntity2));
        matchRepository.save(createMatch(participant1Tournament2, participant2Tournament2, roundEntity3Tournament2, MatchResult.DRAW));

        // When: Invoke the repository method
        List<Integer[]> stats = userRepository.getStatsAgainstUser(comparableUser.getId(), opponentUser.getId());

        // Then: Verify the stats result
        assertThat(stats).isNotNull();
        assertThat(stats.size()).isGreaterThan(0);
        assertThat(stats.get(0)).containsExactly(3, 1, 2);
    }

    @Test
    void testAddReputationCannotBeNegative() {
        UserEntity user = userRepository.save(createUser("test"));
        user.setReputation(10);
        userRepository.save(user);

        {
            userRepository.addReputation(user.getId(), -10);
            UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
            assertThat(updatedUser.getReputation()).isEqualTo(0);
        }

        { // cannot set less than 0
            userRepository.addReputation(user.getId(), -10);
            UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
            assertThat(updatedUser.getReputation()).isEqualTo(0);
        }

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

    private static UserEntity createUser(String username) {
        return UserEntity.builder()
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
