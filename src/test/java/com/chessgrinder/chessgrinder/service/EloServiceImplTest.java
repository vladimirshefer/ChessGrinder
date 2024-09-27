package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.testutil.repository.TestJpaRepository;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EloServiceImplTest {

    private EloServiceImpl eloService;
    private EloCalculationStrategy defaultEloCalculationStrategy;
    private ParticipantRepository participantRepository;
    private TournamentRepository tournamentRepository;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = TestJpaRepository.of(UserRepository.class);

        defaultEloCalculationStrategy = (whiteElo, blackElo, result, bothUsersAuthorized) -> {
            if (result == MatchResult.DRAW) {
                return new EloCalculationStrategy.EloPair(whiteElo, blackElo);
            }
            int points = bothUsersAuthorized ? 10 : 5;
            int playerNewElo = result == MatchResult.WHITE_WIN ? whiteElo + points : whiteElo - points;
            int opponentNewElo = result == MatchResult.WHITE_WIN ? blackElo - points : blackElo + points;
            return new EloCalculationStrategy.EloPair(playerNewElo, opponentNewElo);
        };

        participantRepository = TestJpaRepository.of(ParticipantRepository.class);
        tournamentRepository = TestJpaRepository.of(TournamentRepository.class);
        userRepository = TestJpaRepository.of(UserRepository.class);

        eloService = new EloServiceImpl(defaultEloCalculationStrategy, participantRepository, userRepository, tournamentRepository);
        ReflectionTestUtils.setField(eloService, "eloServiceEnabled", true);
    }

    @Test
    public void testTwoAuthorizedUsersMatch() {
        ParticipantEntity participant1 = createParticipant(createUser("user1", 1200));
        ParticipantEntity participant2 = createParticipant(createUser("user2", 1200));
        MatchEntity match = createMatch(participant1, participant2, MatchResult.WHITE_WIN);
        RoundEntity round = createRound(List.of(match));
        TournamentEntity tournament = createTournament(List.of(round));
        eloService.processTournamentAndUpdateElo(tournament);
        assertElo(participant1, 1200, 10, 1210);
        assertElo(participant2, 1200, -10, 1190);
    }

    @Test
    public void testEloRecalculationAfterMatchResultChange() {
        var participant1 = createParticipant(createUser("user1", 1200));
        var participant2 = createParticipant(createUser("user2", 1200));
        var match = createMatch(participant1, participant2, MatchResult.WHITE_WIN);
        var round = createRound(List.of(match));
        var tournament = createTournament(List.of(round));

        eloService.processTournamentAndUpdateElo(tournament);
        assertElo(participant1, 1200, 10, 1210);
        assertElo(participant2, 1200, -10, 1190);

        eloService.rollbackEloChanges(tournament);
        assertElo(participant1, 1200, 0, 1200);
        assertElo(participant2, 1200, 0, 1200);

        match.setResult(MatchResult.BLACK_WIN);
        eloService.processTournamentAndUpdateElo(tournament);
        assertElo(participant1, 1200, -10, 1190);
        assertElo(participant2, 1200, 10, 1210);
    }

    @Test
    public void testEloRecalculationAfterNoChange() {
        ParticipantEntity participant1 = createParticipant(createUser("user1", 1200));
        ParticipantEntity participant2 = createParticipant(createUser("user2", 1200));
        MatchEntity match = createMatch(participant1, participant2, MatchResult.WHITE_WIN);
        RoundEntity round = createRound(List.of(match));
        TournamentEntity tournament = createTournament(List.of(round));

        eloService.processTournamentAndUpdateElo(tournament);

        assertElo(participant1, 1200, 10, 1210);
        assertElo(participant2, 1200, -10, 1190);

        eloService.rollbackEloChanges(tournament);

        assertElo(participant1, 1200, 0, 1200);
        assertElo(participant2, 1200, 0, 1200);

        match.setResult(MatchResult.WHITE_WIN);
        eloService.processTournamentAndUpdateElo(tournament);

        assertElo(participant1, 1200, 10, 1210);
        assertElo(participant2, 1200, -10, 1190);
    }

    @Test
    public void testAuthorizedVsUnauthorizedUserMatch() {
        ParticipantEntity participant1 = createParticipant(createUser("user1", 1200));
        ParticipantEntity participant2 = createParticipant(null);
        TournamentEntity tournament = createTournament(List.of(
                createRound(List.of(
                        createMatch(participant1, participant2, MatchResult.WHITE_WIN)
                ))
        ));

        eloService.processTournamentAndUpdateElo(tournament);
        assertElo(participant1, 1200, 5, 1205);
    }

    @Test
    public void testDrawMatch() {
        ParticipantEntity participant1 = createParticipant(createUser("user1", 1200));
        ParticipantEntity participant2 = createParticipant(createUser("user2", 1200));
        RoundEntity round = createRound(List.of(
                createMatch(participant1, participant2, MatchResult.DRAW)
        ));
        TournamentEntity tournament = createTournament(List.of(round));

        eloService.processTournamentAndUpdateElo(tournament);
        assertElo(participant1, 1200, 0, 1200);
        assertElo(participant2, 1200, 0, 1200);
    }

    @Test
    public void testGeneralScenarioWithMultipleRounds() {
        ParticipantEntity participant1 = createParticipant(createUser("user1", 1200));
        ParticipantEntity participant2 = createParticipant(createUser("user2", 1200));
        ParticipantEntity participant3 = createGuestParticipant("user3");
        ParticipantEntity participant4 = createGuestParticipant("user4");
        RoundEntity round1 = createRound(List.of(
                createMatch(participant1, participant2, MatchResult.WHITE_WIN),
                createMatch(participant3, participant4, MatchResult.BLACK_WIN)
        ));
        RoundEntity round2 = createRound(List.of(
                createMatch(participant1, participant3, MatchResult.WHITE_WIN),
                createMatch(participant2, participant4, MatchResult.DRAW)
        ));
        TournamentEntity tournament = createTournament(List.of(round1, round2));

        eloService.processTournamentAndUpdateElo(tournament);

        assertElo(participant1, 1200, 15, 1215);
        assertElo(participant2, 1200, -10, 1190);
        assertElo(participant3, 1200, -5, null);
        assertElo(participant4, 1200, 0, null);
    }

    private static TournamentEntity createTournament(List<RoundEntity> rounds) {
        return TournamentEntity.builder()
                .id(UUID.randomUUID())
                .name("Test Tournament")
                .rounds(rounds)
                .status(TournamentStatus.ACTIVE)
                .build();
    }


    private static RoundEntity createRound(List<MatchEntity> matches) {
        return RoundEntity.builder()
                .matches(matches)
                .isFinished(true)
                .build();
    }


    private static MatchEntity createMatch(ParticipantEntity participant1, ParticipantEntity participant2, MatchResult result) {
        return MatchEntity.builder()
                .participant1(participant1)
                .participant2(participant2)
                .result(result)
                .build();
    }

    private ParticipantEntity createParticipant(@Nullable UserEntity user) {
        return participantRepository.save(ParticipantEntity.builder()
                .id(UUID.randomUUID())
                .user(user)
                .nickname(user != null ? user.getUsername() : null)
                .build());
    }

    private ParticipantEntity createGuestParticipant(String nickname) {
        return participantRepository.save(ParticipantEntity.builder()
                .id(UUID.randomUUID())
                .nickname(nickname)
                .build());
    }

    private UserEntity createUser(String username, int elo) {
        return userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .username(username)
                .eloPoints(elo)
                .build());
    }

    private static void assertElo(ParticipantEntity participant1, int initialElo, int diffElo, Integer userAfterElo) {
        String name = participant1.getUser() != null ? participant1.getUser().getUsername() : participant1.getNickname();
        if (participant1.getUser() != null) {
            assertEquals(userAfterElo, participant1.getUser().getEloPoints(), "User final elo is wrong for user " + name);
        } else {
            assertNull(userAfterElo);
        }
        assertEquals(initialElo, participant1.getInitialEloPoints(), "Initial elo is wrong for user " + name);
        assertEquals(diffElo, participant1.getFinalEloPoints(), "Final (Diff) elo is wrong for user " + name);
    }

}
