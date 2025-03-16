package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.chessgrinder.chessgrinder.service.TournamentService.DEFAULT_ROUNDS_NUMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    UserEntity comparableUser;
    UserEntity opponentUser;
    UserEntity thirdWheelUser;

    List<TournamentEntity> allTournaments = new ArrayList<>();

    @BeforeEach
    void setUp() {
        comparableUser = userRepository.save(createUser("comparable"));
        opponentUser = userRepository.save(createUser("opponent"));
        thirdWheelUser = userRepository.save(createUser("thirdWheel"));

        // First tournament setup
        TournamentEntity tournamentEntity1 = tournamentRepository.save(createTournament(TournamentStatus.FINISHED));
        ParticipantEntity participant1Tournament1 = participantRepository.save(createParticipant(comparableUser, tournamentEntity1));
        ParticipantEntity participant2Tournament1 = participantRepository.save(createParticipant(opponentUser, tournamentEntity1));
        RoundEntity roundEntity1Tournament1 = roundRepository.save(createRound(tournamentEntity1));
        matchRepository.save(createMatch(participant1Tournament1, participant2Tournament1, roundEntity1Tournament1, MatchResult.WHITE_WIN));
        RoundEntity roundEntity2Tournament1 = roundRepository.save(createRound(tournamentEntity1));
        matchRepository.save(createMatch(participant1Tournament1, participant2Tournament1, roundEntity2Tournament1, MatchResult.DRAW));
        RoundEntity roundEntity3Tournament1 = roundRepository.save(createRound(tournamentEntity1));
        matchRepository.save(createMatch(participant1Tournament1, participant2Tournament1, roundEntity3Tournament1, MatchResult.BLACK_WIN));
        allTournaments.add(tournamentEntity1);

        // Second tournament setup
        TournamentEntity tournamentEntity2 = tournamentRepository.save(createTournament(TournamentStatus.FINISHED));
        ParticipantEntity participant1Tournament2 = participantRepository.save(createParticipant(comparableUser, tournamentEntity2));
        ParticipantEntity participant2Tournament2 = participantRepository.save(createParticipant(opponentUser, tournamentEntity2));
        RoundEntity roundEntity1Tournament2 = roundRepository.save(createRound(tournamentEntity2));
        matchRepository.save(createMatch(participant1Tournament2, participant2Tournament2, roundEntity1Tournament2, MatchResult.WHITE_WIN));
        RoundEntity roundEntity2Tournament2 = roundRepository.save(createRound(tournamentEntity2));
        matchRepository.save(createMatch(participant2Tournament2, participant1Tournament2, roundEntity2Tournament2, MatchResult.BLACK_WIN));
        RoundEntity roundEntity3Tournament2 = roundRepository.save(createRound(tournamentEntity2));
        matchRepository.save(createMatch(participant1Tournament2, participant2Tournament2, roundEntity3Tournament2, MatchResult.DRAW));
        allTournaments.add(tournamentEntity2);

        // Unfinished tournament shouldn't be in the result
        TournamentEntity tournamentEntity3 = tournamentRepository.save(createTournament(TournamentStatus.ACTIVE));
        ParticipantEntity participant1Tournament3 = participantRepository.save(createParticipant(comparableUser, tournamentEntity3));
        ParticipantEntity participant2Tournament3 = participantRepository.save(createParticipant(opponentUser, tournamentEntity3));
        RoundEntity roundEntity1Tournament3 = roundRepository.save(createRound(tournamentEntity3));
        matchRepository.save(createMatch(participant1Tournament3, participant2Tournament3, roundEntity1Tournament3, MatchResult.DRAW));
        RoundEntity roundEntity2Tournament3 = roundRepository.save(createRound(tournamentEntity3));
        matchRepository.save(createMatch(participant2Tournament3, participant1Tournament3, roundEntity2Tournament3, MatchResult.DRAW));
        RoundEntity roundEntity3Tournament3 = roundRepository.save(createRound(tournamentEntity3));
        matchRepository.save(createMatch(participant1Tournament3, participant2Tournament3, roundEntity3Tournament3, MatchResult.WHITE_WIN));
        allTournaments.add(tournamentEntity3);
    }

    @Test
    void testStatsVsUser() {
        // When: Invoke the repository method
        List<Integer[]> stats = userRepository.getStatsAgainstUser(comparableUser.getId(), opponentUser.getId());

        // Then: Verify the stats result
        assertNotNull(stats);

        int wins = countMatchesByResult(comparableUser.getId(), opponentUser.getId(), 1);
        int losses = countMatchesByResult(comparableUser.getId(), opponentUser.getId(), -1);
        int draws = countMatchesByResult(comparableUser.getId(), opponentUser.getId(), 0);
        assertThat(stats.get(0)).containsExactly(wins, losses, draws);
    }

    /**
     * Test where are users who never played with each other
     */
    @Test
    void testWithZeroMatches() {
        List<Integer[]> stats = userRepository.getStatsAgainstUser(comparableUser.getId(), thirdWheelUser.getId());

        assertNotNull(stats);
        int wins = countMatchesByResult(comparableUser.getId(), thirdWheelUser.getId(), 1);
        int losses = countMatchesByResult(comparableUser.getId(), thirdWheelUser.getId(), -1);
        int draws = countMatchesByResult(comparableUser.getId(), thirdWheelUser.getId(), 0);
        assertThat(stats.get(0)).containsExactly(wins, losses, draws);
    }

    /**
     * Calculate number of wins, losses and draws of comparableUserId with opponentUserId
     * @param comparableUserId first user id
     * @param opponentUserId second user id
     * @param calc calculate wins, losses or draws
     * @return number of wins, losses and draws
     */
    private int countMatchesByResult(UUID comparableUserId, UUID opponentUserId, int calc) {
        return (int) allTournaments.stream()
                .filter(t -> t.getStatus() == TournamentStatus.FINISHED)
                .flatMap(t -> t.getRounds().stream()) // Expand all rounds
                .flatMap(r -> r.getMatches().stream()) // Expand all matches
                .filter(m -> {
                    ParticipantEntity p1 = m.getParticipant1();
                    ParticipantEntity p2 = m.getParticipant2();

                    if (p1 == null || p2 == null || p1.getUser() == null || p2.getUser() == null) {
                        return false; // Check participants without user
                    }

                    UUID user1 = p1.getUser().getId();
                    UUID user2 = p2.getUser().getId();
                    MatchResult result = m.getResult();

                    if (calc == 1) { // First wins
                        return (user1.equals(comparableUserId) && user2.equals(opponentUserId) && result == MatchResult.WHITE_WIN) ||
                                (user2.equals(comparableUserId) && user1.equals(opponentUserId) && result == MatchResult.BLACK_WIN);
                    }
                    else if (calc == -1) { //Second wins
                        return (user1.equals(comparableUserId) && user2.equals(opponentUserId) && result == MatchResult.BLACK_WIN) ||
                                (user2.equals(comparableUserId) && user1.equals(opponentUserId) && result == MatchResult.WHITE_WIN);
                    }
                    else { // Nobody wins (draw)
                        return (user1.equals(comparableUserId) && user2.equals(opponentUserId) ||
                                user2.equals(comparableUserId) && user1.equals(opponentUserId))
                                && m.getResult() == MatchResult.DRAW;
                    }
                })
                .count();
    }


    private static MatchEntity createMatch(ParticipantEntity comparableUserParticipant, ParticipantEntity opponentUserParticipant, RoundEntity roundEntity, MatchResult matchResult) {
        MatchEntity match = MatchEntity.builder()
                .participant1(comparableUserParticipant)
                .participant2(opponentUserParticipant)
                .result(matchResult)
                .round(roundEntity)
                .build();

        roundEntity.addMatch(match);
        return match;
    }

    private static RoundEntity createRound(TournamentEntity tournamentEntity) {
        var round = RoundEntity.builder()
                .tournament(tournamentEntity)
                .isFinished(true)
                .build();

        tournamentEntity.addRound(round);
        return round;
    }

    private static TournamentEntity createTournament(TournamentStatus tStatus) {
        return TournamentEntity.builder()
                .status(tStatus)
                .date(LocalDateTime.now())
                .roundsNumber(DEFAULT_ROUNDS_NUMBER)
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
