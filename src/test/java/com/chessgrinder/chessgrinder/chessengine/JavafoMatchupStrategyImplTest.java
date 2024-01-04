package com.chessgrinder.chessgrinder.chessengine;

import com.chessgrinder.chessgrinder.enums.MatchResult;
import org.junit.jupiter.api.Test;

import static com.chessgrinder.chessgrinder.chessengine.SwissMatchupStrategyImplTest.participant;
import static com.chessgrinder.chessgrinder.chessengine.SwissMatchupStrategyImplTest.runTournament;

class JavafoMatchupStrategyImplTest {

    MatchupStrategy swissEngine = new JavafoMatchupStrategyImpl();

    @Test
    void test3p() {
        runTournament(swissEngine, "user1", "user2", "user3", "user4", "user5")
                .thenRound(round -> round
                        .match(participant("user1", 1, 0), participant("user3", 0, 1), MatchResult.WHITE_WIN)
                        .match(participant("user4", 1, 0), participant("user2", 0, 1), MatchResult.WHITE_WIN)
                        .match(participant("user5", 1, 0), null, MatchResult.BUY)
                )
                .show(System.out::println)
                .thenRound(round -> round
                        .match(participant("user5", 2, 1), participant("user1", 1, 3), MatchResult.WHITE_WIN)
                        .match(participant("user3", 1, 2), participant("user4", 1, 2), MatchResult.WHITE_WIN)
                        .match(participant("user2", 1, 1), null, MatchResult.BUY)
                )
                .show(System.out::println);
    }

    @Test
    void testPrint() {

    }
}
