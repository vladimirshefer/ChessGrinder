package com.chessgrinder.chessgrinder.chessengine;

import com.chessgrinder.chessgrinder.chessengine.pairings.JavafoPairingStrategyImpl;
import com.chessgrinder.chessgrinder.chessengine.pairings.PairingStrategy;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import org.junit.jupiter.api.Test;

import static com.chessgrinder.chessgrinder.chessengine.MockTournamentRunnerUtils.participant;
import static com.chessgrinder.chessgrinder.chessengine.MockTournamentRunnerUtils.runTournament;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JavafoPairingStrategyImplTest {

    PairingStrategy swissEngine = new JavafoPairingStrategyImpl();

    @Test
    void test5p() {
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
    void test2pOverflown() {
        MockTournamentRunner tournament = runTournament(swissEngine, "user1", "user2")
                .thenRound(round -> round
                        .match(participant("user1", 1, 0), participant("user2", 0, 1), MatchResult.WHITE_WIN)
                )
                .show(System.out::println);
        // fails because players already played
        assertThrows(Exception.class, () ->
                tournament
                        .thenRound(round -> round
                                .match(participant("user2", 1, 1), participant("user1", 1, 1), MatchResult.WHITE_WIN)
                        )
        );
    }

    @Test
    void test1p() {
        runTournament(swissEngine, "user1")
                .thenRound(round -> round
                        .match(participant("user1", 1, 0), null, MatchResult.BUY)
                )
                .show(System.out::println);
    }


    @Test
    void testNewcomers() {
        runTournament(swissEngine, "user1", "user2", "user3", "user4", "user5", "user6")
                .thenRound(round -> round
                        .match(participant("user1", 1, 0), participant("user4", 0, 1), MatchResult.WHITE_WIN)
                        .match(participant("user5", 0.5, 0.5), participant("user2", 0.5, 0.5), MatchResult.DRAW)
                        .match(participant("user3", 0.5, 0.5), participant("user6", 0.5, 0.5), MatchResult.DRAW)
                )
                .show(System.out::println)
                .thenRound(round -> round
                        .match(participant("user2", 0.5, 3), participant("user1", 2, 1.5), MatchResult.BLACK_WIN)
                        .match(participant("user6", 1, 1.5), participant("user5", 1, 1.5), MatchResult.DRAW)
                        .match(participant("user4", 1, 2.5), participant("user3", 0.5, 2), MatchResult.WHITE_WIN)
                )
                .newParticipant("user7")
                .show(System.out::println)
                .thenRound(round -> round
                        .match("user1", "user6", MatchResult.WHITE_WIN)
                        .match("user5", "user4", MatchResult.WHITE_WIN)
                        .match("user3", "user2", MatchResult.WHITE_WIN)
                        .match("user7", null, MatchResult.BUY)
                )
                .show(System.out::println)
                .newParticipant("user8")
                .thenRound(round -> round
                        .match("user5", "user1", MatchResult.WHITE_WIN)
                        .match("user7", "user3", MatchResult.WHITE_WIN)
                        .match("user4", "user6", MatchResult.WHITE_WIN)
                        .match("user2", "user8", MatchResult.WHITE_WIN)
                )
                .show(System.out::println)
                .missParticipant("user8")
                .thenRound(round -> round
                        .match("user1", "user7", MatchResult.WHITE_WIN)
                        .match("user3", "user5", MatchResult.DRAW)
                        .match("user2", "user4", MatchResult.BLACK_WIN)
                        .match("user6", null, MatchResult.BUY)
                        .match("user8", null, MatchResult.MISS))
                .show(System.out::println)
                .returnParticipant("user8")
                .thenRound(round -> round
                        .match("user3", "user1", MatchResult.BLACK_WIN)
                        .match("user7", "user5", MatchResult.WHITE_WIN)
                        .match("user4", "user8", MatchResult.DRAW)
                        .match("user6", "user2", MatchResult.DRAW))
                .show(System.out::println)
                .showParticipants(System.out::println);
    }
}
