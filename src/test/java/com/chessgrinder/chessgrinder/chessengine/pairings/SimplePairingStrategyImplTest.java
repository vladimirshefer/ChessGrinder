package com.chessgrinder.chessgrinder.chessengine.pairings;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.Player001TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.util.TrfUtil;
import com.chessgrinder.chessgrinder.testutil.trf.TrfTournamentGenerator;
import com.chessgrinder.chessgrinder.testutil.trf.TrfTournamentRunner;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chessgrinder.chessgrinder.testutil.trf.TrfTestUtil.assertTrfEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SimplePairingStrategyImplTest {

    private PairingStrategy pairingStrategy = new SimplePairingStrategyImpl();

    @Test
    void testBasicPairingsWithFivePlayersAndMatchResults() {
        String trf = """
                XXR 5
                001    1      player01                          1000                             0.0          2 w 1     3 w 1     4 b 1     5 b 1  0000 - U
                001    2      player02                          1000                             0.0          1 b 0     5 w 1  0000 - U     4 w 1     3 b 1
                001    3      player03                          1000                             0.0          4 w 1     1 b 0     5 b 1  0000 - U     2 w 0
                001    4      player04                          1000                             0.0          3 b 0  0000 - U     1 w 0     2 b 0     5 w 1
                001    5      player05                          1000                             0.0       0000 - U     2 b 0     3 w 0     1 w 0     4 b 0
                """;
        TrfTournamentRunner.verify(pairingStrategy, TrfUtil.readTrf(trf));
    }

    @Test
    void test2() {
        List<TrfLine> trf = TrfTournamentGenerator.runTournament(
                pairingStrategy,
                TrfTournamentGenerator.initTournament(11, 10)
        );

        assertEquals("""
                XXR 10
                001    1      Player1                                                            4.0          2 w 0     3 w 0     4 b 1     5 w 0     6 b 1     7 b 1     8 w 0     9 b 1    10 w 0    11 b 1 \s
                001    2      Player2                                                            4.0          1 b 1     4 w 0     3 w 0     6 w 0     5 b 1     8 b 1     7 w 0    10 b 1     9 w 0  0000 - U \s
                001    3      Player3                                                            5.0          4 w 0     1 b 1     2 b 1     9 w 0    10 b 1    11 b 1  0000 - U     7 w 0     8 w 0     5 w 0 \s
                001    4      Player4                                                            5.0          3 b 1     2 b 1     1 w 0    10 w 0     9 b 1  0000 - U    11 w 0     8 w 0     7 b 1     6 w 0 \s
                001    5      Player5                                                            5.0          6 w 0     7 w 0     8 b 1     1 b 1     2 w 0     9 b 1    10 w 0    11 b 1  0000 - U     3 b 1 \s
                001    6      Player6                                                            5.0          5 b 1     8 w 0     7 w 0     2 b 1     1 w 0    10 b 1     9 w 0  0000 - U    11 b 1     4 b 1 \s
                001    7      Player7                                                            5.0          8 w 0     5 b 1     6 b 1    11 w 0  0000 - U     1 w 0     2 b 1     3 b 1     4 w 0     9 w 0 \s
                001    8      Player8                                                            6.0          7 b 1     6 b 1     5 w 0  0000 - U    11 w 0     2 w 0     1 b 1     4 b 1     3 b 1    10 w 0 \s
                001    9      Player9                                                            5.0         10 w 0    11 b 1  0000 - U     3 b 1     4 w 0     5 w 0     6 b 1     1 w 0     2 b 1     7 b 1 \s
                001   10      Player10                                                           5.0          9 b 1  0000 - U    11 w 0     4 b 1     3 w 0     6 w 0     5 b 1     2 w 0     1 b 1     8 b 1 \s
                001   11      Player11                                                           5.0       0000 - U     9 w 0    10 b 1     7 b 1     8 b 1     3 w 0     4 b 1     5 w 0     6 w 0     1 w 0 \s
                """, TrfUtil.writeTrfLines(trf));
    }

    @Test
    void testOverflow() {
        List<TrfLine> trf = TrfTournamentGenerator.runTournament(
                pairingStrategy,
                TrfTournamentGenerator.initTournament(5, 10),
                (w, b) -> {
                    if (w ==0 || b==0) {
                        return Player001TrfLine.TrfMatchResult.PAIRING_ALLOCATED_BYE;
                    }
                    return switch ((w + b) % 3) {
                        case 0 -> Player001TrfLine.TrfMatchResult.WIN;
                        case 1 -> Player001TrfLine.TrfMatchResult.DRAW;
                        case 2 -> Player001TrfLine.TrfMatchResult.LOSS;
                        default -> throw new IllegalStateException("Unexpected value: " + (w + b) % 3);
                    };
                }
        );

        assertTrfEquals("""
                001    1      Player1                                                            6.0          2 w 1     3 w =     4 b 1     5 b 0  0000 - U     2 w 1     3 w =     4 b 1     5 b 0  0000 - U
                001    2      Player2                                                            6.0          1 b 0     5 w =  0000 - U     4 w 1     3 b 1     1 b 0     5 w =  0000 - U     4 w 1     3 b 1
                001    3      Player3                                                            6.0          4 w =     1 b =     5 b 1  0000 - U     2 w 0     4 w =     1 b =     5 b 1  0000 - U     2 w 0
                001    4      Player4                                                            4.0          3 b =  0000 - U     1 w 0     2 b 0     5 w 1     3 b =  0000 - U     1 w 0     2 b 0     5 w 1
                001    5      Player5                                                            5.0       0000 - U     2 b =     3 w 0     1 w 1     4 b 0  0000 - U     2 b =     3 w 0     1 w 1     4 b 0
                XXR 10
                """, trf);
    }

    /**
     * Checks that pairing strategy handles invalid tournament data gracefully.
     * Tests scenarios with:
     * - Zero planned rounds
     * - Impossible match results
     * - Invalid pairings
     * - Different match results
     * - Duplicate player assignments
     */
    @Test
    void testCrazyInput() {
        // What is wrong: 0 planned rounds. Players' pairings and results are random and impossible.
        List<TrfLine> trf = TrfUtil.readTrf("""
                XXR 0
                001    1      Player1                                                            6.0          2 w 1     3 w =     4 w 1
                001    2      Player2                                                            5.0       0000 w 0  0000 - U  0000 - U
                001    3      Player3                                                            6.0          4 w U     1 w =     5 w 1
                001    4      Player4                                                            5.0          5 w =  0000 - U     5 w F
                001    5      Player5                                                            5.0       0000 - U     5 w =     5 w 0
                """);
        TrfTournamentGenerator.runRound(
                pairingStrategy,
                trf
        );
        Set<Integer> pairedPlayers = TrfUtil.getPairings(trf, 4).entrySet().stream().flatMap(it -> Stream.of(it.getKey(), it.getValue())).collect(Collectors.toSet());
        assertEquals(Set.of(1, 2, 3, 4, 5, 0), pairedPlayers);
    }

}
