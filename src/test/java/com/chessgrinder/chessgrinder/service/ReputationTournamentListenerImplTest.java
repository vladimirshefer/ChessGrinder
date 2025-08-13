package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.repositories.*;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityManager;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ReputationTournamentListenerImplTest {

    private ReputationTournamentListenerImpl reputationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @BeforeEach
    public void setUp() {
        reputationService = new ReputationTournamentListenerImpl(userRepository);
    }

    @Test
    @Transactional
    public void testAll() {
        UserEntity uA = createUser("A");
        UserEntity uB = createUser("B");
        UserEntity uC = createUser("C");
        UserEntity uD = createUser("D");
        UserEntity uE = createUser("E");

        TournamentEntity tournament1 = withTransaction(() -> {
            var t = createTournament();
            var pA = createParticipant(t, uA);
            var pB = createParticipant(t, uB);
            var pC = createParticipant(t, uC);
            var pD = createParticipant(t, uD);
            var pE = createParticipant(t, uE);
            var pF = createParticipant(t, null);
            var t1r1 = createRound(t, 1);
            createMatch(t1r1, pA, pB, MatchResult.WHITE_WIN);
            createMatch(t1r1, pC, pD, MatchResult.WHITE_WIN);
            createMatch(t1r1, pE, pF, MatchResult.WHITE_WIN);
            var t1r2 = createRound(t, 2);
            createMatch(t1r2, pB, pA, MatchResult.DRAW);
            createMatch(t1r2, pD, pC, MatchResult.DRAW);
            createMatch(t1r2, pF, pE, MatchResult.DRAW);
            var t1r3 = createRound(t, 3);
            createMatch(t1r3, pA, null, MatchResult.BUY);
            createMatch(t1r3, pB, null, MatchResult.MISS);
            createMatch(t1r3, null, pC, MatchResult.MISS);
            entityManager.flush();
            t = tournamentRepository.findById(t.getId()).orElseThrow();
            return t;
        });

        TournamentEntity tournament2 = withTransaction(() -> {
            var t = createTournament();
            var pA = createParticipant(t, uA);
            var pB = createParticipant(t, uB);
            var pC = createParticipant(t, uC);
            var pD = createParticipant(t, uD);
            var pE = createParticipant(t, uE);
            var pF = createParticipant(t, null);
            // A - 5 wins. B - 5 losses.
            var t2r1 = createRound(t, 1);
            createMatch(t2r1, pA, pB, MatchResult.WHITE_WIN);
            createMatch(t2r1, pA, pC, MatchResult.WHITE_WIN);
            createMatch(t2r1, pA, pD, MatchResult.WHITE_WIN);
            createMatch(t2r1, pA, pE, MatchResult.WHITE_WIN);
            createMatch(t2r1, pA, pF, MatchResult.WHITE_WIN);
            createMatch(t2r1, pC, pB, MatchResult.WHITE_WIN);
            createMatch(t2r1, pD, pB, MatchResult.WHITE_WIN);
            createMatch(t2r1, pE, pB, MatchResult.WHITE_WIN);
            createMatch(t2r1, pF, pB, MatchResult.WHITE_WIN);
            entityManager.flush();
            t = tournamentRepository.findById(t.getId()).orElseThrow();
            return t;
        });

        entityManager.refresh(tournament1);
        entityManager.refresh(tournament2);

        reputationService.tournamentFinished(tournament1);

        assertReputation(refresh(uA), 3);
        assertReputation(refresh(uB), 1);
        assertReputation(refresh(uC), 2);
        assertReputation(refresh(uD), 1);
        assertReputation(refresh(uE), 2);

        reputationService.tournamentFinished(tournament2);

        assertReputation(refresh(uA), 3 + 6);
        assertReputation(refresh(uB), 1 + 4);
        assertReputation(refresh(uC), 2 + 2);
        assertReputation(refresh(uD), 1 + 2);
        assertReputation(refresh(uE), 2 + 2);

        reputationService.tournamentReopened(tournament1);
        assertReputation(refresh(uA), 6);
        assertReputation(refresh(uB), 4);
        assertReputation(refresh(uC), 2);
        assertReputation(refresh(uD), 2);
        assertReputation(refresh(uE), 2);

        reputationService.totalReset();

        assertReputation(refresh(uA), 0);
        assertReputation(refresh(uB), 0);
        assertReputation(refresh(uC), 0);
        assertReputation(refresh(uD), 0);
        assertReputation(refresh(uE), 0);
    }

    private UserEntity refresh(UserEntity entity) {
        return userRepository.findById(entity.getId()).orElseThrow();
    }

    private <T> T refresh(T entity) {
        entityManager.refresh(entity);
        return entity;
    }

    private TournamentEntity createTournament() {
        return tournamentRepository.save(TournamentEntity.builder()
                .id(UUID.randomUUID())
                .name("Test Tournament")
                .roundsNumber(999)
                .rounds(new ArrayList<>())
                .status(TournamentStatus.ACTIVE)
                .build()
        );
    }

    private RoundEntity createRound(TournamentEntity tournament, int number) {
        RoundEntity round = roundRepository.save(RoundEntity.builder()
                .tournament(tournament)
                .number(number)
                .matches(new ArrayList<>())
                .isFinished(true)
                .build()
        );
        tournament.getRounds().add(round);
        return round;
    }

    private MatchEntity createMatch(RoundEntity round, @Nullable ParticipantEntity participant1, @Nullable ParticipantEntity participant2, MatchResult result) {
        MatchEntity match = matchRepository.save(MatchEntity.builder()
                .participant1(participant1)
                .participant2(participant2)
                .round(round)
                .result(result)
                .build()
        );
        round.getMatches().add(match);
        return match;
    }

    private ParticipantEntity createParticipant(TournamentEntity tournament, @Nullable UserEntity user) {
        return participantRepository.save(ParticipantEntity.builder()
                .id(UUID.randomUUID())
                .user(user)
                .score(BigDecimal.ZERO)
                .buchholz(BigDecimal.ZERO)
                .tournament(tournament)
                .place(0)
                .nickname(user != null ? user.getUsername() : UUID.randomUUID().toString())
                .build()
        );
    }

    private UserEntity createUser(String username) {
        return userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .username(username)
                .reputation(0)
                .build()
        );
    }

    @SneakyThrows
    private <T> T withTransaction(Callable<T> runnable) {
        var status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            T result = runnable.call();
            transactionManager.commit(status);
            return result;
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }

    private static void assertReputation(UserEntity user, int expected) {
        assertNotNull(user, "User must not be null for reputation assertions");
        assertEquals(expected, user.getReputation(), "Unexpected reputation for user " + user.getUsername());
    }

    @TestConfiguration
    static class AuditorAwareTestConfig {
        @Bean
        public AuditorAware<String> auditorProvider() {
            return () -> Optional.of("test_user");
        }
    }
}
