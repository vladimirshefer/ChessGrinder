package com.chessgrinder.chessgrinder.chessengine;

import com.chessgrinder.chessgrinder.enums.MatchResult;
import org.junit.jupiter.api.Test;

import static com.chessgrinder.chessgrinder.chessengine.SwissPairingStrategyImplTest.*;

public class RoundRobinPairingStrategyImplTest {

    PairingStrategy swissEngine = new RoundRobinPairingStrategyImpl();

    @Test
    void test3p() {
        runTournament(swissEngine, "user1", "user2", "user3")
                .thenRound(round -> round
                        .match(participant("user1", 1, 0), null, MatchResult.BUY)
                        .match(participant("user2", 1, 0), participant("user3", 0, 1), MatchResult.WHITE_WIN)

                )
                .show(System.out::println)
                .thenRound(round -> round
                        .match(null, participant("user3", 1, 1), MatchResult.BUY)
                        .match(participant("user1", 2, 1), participant("user2", 1, 3), MatchResult.WHITE_WIN)
                )
                .show(System.out::println)

                .thenRound(round -> round
                        .match(participant("user2", 2, 4), null, MatchResult.BUY)
                        .match(participant("user3", 2, 4), participant("user1", 2, 4), MatchResult.WHITE_WIN)
                )
                .show(System.out::println);
    }

    @Test
    void test6p() {
        {
            runTournament(swissEngine, "user1", "user2", "user3", "user4", "user5", "user6")
                    .thenRound(round -> round
                            .match(participant("user1", 1, 0), participant("user6", 0, 1), MatchResult.WHITE_WIN)
                            .match(participant("user2", 1, 0), participant("user5", 0, 1), MatchResult.WHITE_WIN)
                            .match(participant("user3", 1, 0), participant("user4", 0, 1), MatchResult.WHITE_WIN)
                    )
                    .show(System.out::println)

                    .thenRound(round -> round
                            .match(participant("user6", 1, 2), participant("user4", 0, 2), MatchResult.WHITE_WIN)
                            .match(participant("user5", 1, 2), participant("user3", 1, 1), MatchResult.WHITE_WIN)
                            .match(participant("user1", 2, 2), participant("user2", 1, 3), MatchResult.WHITE_WIN)
                    )
                    .show(System.out::println)

                    .thenRound(round -> round
                            .match(participant("user2", 2, 4), participant("user6", 1, 5), MatchResult.WHITE_WIN)
                            .match(participant("user3", 2, 4), participant("user1", 2, 5), MatchResult.WHITE_WIN)
                            .match(participant("user4", 1, 4), participant("user5", 1, 5), MatchResult.WHITE_WIN)
                    )
                    .show(System.out::println)

                    .thenRound(round -> round
                            .match(participant("user6", 2, 8), participant("user5", 1, 8), MatchResult.WHITE_WIN)
                            .match(participant("user1", 3, 8), participant("user4", 1, 8), MatchResult.WHITE_WIN)
                            .match(participant("user2", 3, 8), participant("user3", 2, 8), MatchResult.WHITE_WIN)
                    )
                    .show(System.out::println)

                    .thenRound(round -> round
                            .match(participant("user3", 3, 12), participant("user6", 2, 13), MatchResult.WHITE_WIN)
                            .match(participant("user4", 2, 13), participant("user2", 3, 12), MatchResult.WHITE_WIN)
                            .match(participant("user5", 2, 13), participant("user1", 3, 12), MatchResult.WHITE_WIN)
                    )
                    .show(System.out::println);


        }
    }
}
