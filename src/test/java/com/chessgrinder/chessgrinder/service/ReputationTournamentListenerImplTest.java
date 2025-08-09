package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.testutil.repository.TestJpaRepository;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class ReputationTournamentListenerImplTest {

    private ReputationTournamentListenerImpl reputationService;
    private UserRepository userRepository;
    private ParticipantRepository participantRepository;

    @BeforeEach
    public void setUp() {
        userRepository = Mockito.spy(TestJpaRepository.of(UserRepository.class));
        Mockito.doAnswer(i -> {
            UUID userId = i.getArgument(0, UUID.class);
            int reputationChange = i.getArgument(1, Integer.class);
            Map<UUID, UserEntity> data = TestJpaRepository.getData(userRepository);
            UserEntity userEntity = data.get(userId);
            userEntity.setReputation(userEntity.getReputation() + reputationChange);
            return null;
        }).when(userRepository).addReputation(any(), any());

        Mockito.doAnswer(i -> {
            Map<UUID, UserEntity> data = TestJpaRepository.getData(userRepository);
            for (UserEntity user : data.values()) {
                user.setReputation(0);
            }
            return null;
        }).when(userRepository).clearAllReputation();

        participantRepository = Mockito.spy(TestJpaRepository.of(ParticipantRepository.class));
        reputationService = new ReputationTournamentListenerImpl(userRepository);
    }

    @Test
    public void testAll() {
        UserEntity uA = createUser("A");
        UserEntity uB = createUser("B");
        UserEntity uC = createUser("C");
        UserEntity uD = createUser("D");
        UserEntity uE = createUser("E");


        TournamentEntity tournament1;
        {
            var pA = createParticipant(uA);
            var pB = createParticipant(uB);
            var pC = createParticipant(uC);
            var pD = createParticipant(uD);
            var pE = createParticipant(uE);
            var pF = createParticipant(null);
            var t1r1 = createRound(List.of(
                    createMatch(pA, pB, MatchResult.WHITE_WIN),
                    createMatch(pC, pD, MatchResult.WHITE_WIN),
                    createMatch(pE, pF, MatchResult.WHITE_WIN)
            ));
            var t1r2 = createRound(List.of(
                    createMatch(pB, pA, MatchResult.DRAW),
                    createMatch(pD, pC, MatchResult.DRAW),
                    createMatch(pF, pE, MatchResult.DRAW)
            ));
            var t1r3 = createRound(List.of(
                    createMatch(pA, null, MatchResult.BUY),
                    createMatch(pB, null, MatchResult.MISS),
                    createMatch(null, pC, MatchResult.MISS)
            ));

            tournament1 = createTournament(List.of(t1r1, t1r2, t1r3));
        }

        TournamentEntity tournament2;
        {
            var pA = createParticipant(uA);
            var pB = createParticipant(uB);
            var pC = createParticipant(uC);
            var pD = createParticipant(uD);
            var pE = createParticipant(uE);
            var pF = createParticipant(null);
            // A - 5 wins. B - 5 losses.
            var t1r1 = createRound(List.of(
                    createMatch(pA, pB, MatchResult.WHITE_WIN),
                    createMatch(pA, pC, MatchResult.WHITE_WIN),
                    createMatch(pA, pD, MatchResult.WHITE_WIN),
                    createMatch(pA, pE, MatchResult.WHITE_WIN),
                    createMatch(pA, pF, MatchResult.WHITE_WIN),
                    createMatch(pC, pB, MatchResult.WHITE_WIN),
                    createMatch(pD, pB, MatchResult.WHITE_WIN),
                    createMatch(pE, pB, MatchResult.WHITE_WIN),
                    createMatch(pF, pB, MatchResult.WHITE_WIN)
            ));
            tournament2 = createTournament(List.of(t1r1));
        }

        reputationService.tournamentFinished(tournament1);

        assertReputation(uA, 3);
        assertReputation(uB, 1);
        assertReputation(uC, 2);
        assertReputation(uD, 1);
        assertReputation(uE, 2);

        reputationService.tournamentFinished(tournament2);

        assertReputation(uA, 3 + 6);
        assertReputation(uB, 1 + 4);
        assertReputation(uC, 2 + 2);
        assertReputation(uD, 1 + 2);
        assertReputation(uE, 2 + 2);

        reputationService.tournamentReopened(tournament1);
        assertReputation(uA, 6);
        assertReputation(uB, 4);
        assertReputation(uC, 2);
        assertReputation(uD, 2);
        assertReputation(uE, 2);

        reputationService.totalReset();

        assertReputation(uA, 0);
        assertReputation(uB, 0);
        assertReputation(uC, 0);
        assertReputation(uD, 0);
        assertReputation(uE, 0);
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

    private static MatchEntity createMatch(@Nullable ParticipantEntity participant1, @Nullable ParticipantEntity participant2, MatchResult result) {
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

    private UserEntity createUser(String username) {
        return userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .username(username)
                .reputation(0)
                .build()
        );
    }

    private static void assertReputation(UserEntity user, int expected) {
        assertNotNull(user, "User must not be null for reputation assertions");
        assertEquals(expected, user.getReputation(), "Unexpected reputation for user " + user.getUsername());
    }
}
