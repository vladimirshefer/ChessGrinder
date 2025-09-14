package com.chessgrinder.chessgrinder.chessengine.pairings;

import com.chessgrinder.chessgrinder.chessengine.PairingFailedException;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.MissingPlayersXxzTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.util.TrfUtil;
import com.chessgrinder.chessgrinder.testutil.trf.TrfTournamentGenerator;
import com.chessgrinder.chessgrinder.testutil.trf.TrfTournamentRunner;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static com.chessgrinder.chessgrinder.testutil.trf.TrfTestUtil.assertTrfEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JaVaFoPairingStrategyImplTest {

    PairingStrategy swissEngine = new JaVaFoPairingStrategyImpl();

    @Test
    void test5p() {
        var trf = """
                XXR 2
                001    1      user1                             1000                             1.0          3 w 1     5 b 0
                001    2      user2                             1000                             1.0          4 b 0  0000 - U
                001    3      user3                             1000                             1.0          1 b 0     4 w 1
                001    4      user4                             1000                             1.0          2 w 1     3 b 0
                001    5      user5                             1000                             2.0       0000 - U     1 w 1
                """;
        TrfTournamentRunner.verify(swissEngine, TrfUtil.readTrf(trf));
    }

    @Test
    void test2pOverflown() {
        List<TrfLine> trfLines = TrfTournamentGenerator.initTournament(2, 10);
        TrfTournamentGenerator.runRound(swissEngine, trfLines);
        assertThrows(PairingFailedException.class, () -> TrfTournamentGenerator.runRound(swissEngine, trfLines));
    }

    @Test
    void test1p() {
        var trf = """
                XXR 2
                001    1      user1                             1000                             0.0       0000 - U
                """;
        TrfTournamentRunner.verify(swissEngine, TrfUtil.readTrf(trf));
    }

    @Test
    void testNewcomers() {
        List<TrfLine> initialTrf = TrfTournamentGenerator.initTournament(6, 10);
        TrfTournamentGenerator.runRound(swissEngine, initialTrf);
        TrfTournamentGenerator.runRound(swissEngine, initialTrf);
        initialTrf.add(TrfTournamentGenerator.generatePlayer(7));
        TrfTournamentGenerator.runRound(swissEngine, initialTrf);
        initialTrf.add(MissingPlayersXxzTrfLine.of(Collections.singletonList(1)));
        TrfTournamentGenerator.runRound(swissEngine, initialTrf);
        initialTrf.add(MissingPlayersXxzTrfLine.of(Collections.singletonList(2)));
        TrfTournamentGenerator.runRound(swissEngine, initialTrf);
        initialTrf.add(TrfTournamentGenerator.generatePlayer(8));
        initialTrf.add(TrfTournamentGenerator.generatePlayer(9));
        initialTrf.add(TrfTournamentGenerator.generatePlayer(10));
        TrfTournamentGenerator.runRound(swissEngine, initialTrf);
        assertTrfEquals("""
                XXR 10
                XXZ 1
                XXZ 2
                001    1      Player1                                                            1.0          4 w 0     6 b 1     2 w 0  0000 - Z  0000 - Z  0000 - Z
                001    2      Player2                                                            2.0          5 b 1     4 w 0     1 b 1     6 w 0  0000 - Z  0000 - Z
                001    3      Player3                                                            3.0          6 w 0     5 b 1     7 w 0     4 b 1  0000 - U     8 w 0
                001    4      Player4                                                            3.0          1 b 1     2 b 1     6 w 0     3 w 0     7 b 1     5 b 1
                001    5      Player5                                                            3.0          2 w 0     3 w 0  0000 - U     7 b 1     6 b 1     4 w 0
                001    6      Player6                                                            3.0          3 b 1     1 w 0     4 b 1     2 b 1     5 w 0     7 w 0
                001    7      Player7                                                            1.0       0000 - Z  0000 - Z     3 b 1     5 w 0     4 w 0     6 b 1
                001    8      Player8                                                            0.0       0000 - Z  0000 - Z  0000 - Z  0000 - Z  0000 - Z     3 b 1
                001    9      Player9                                                            0.0       0000 - Z  0000 - Z  0000 - Z  0000 - Z  0000 - Z    10 w 0
                001   10      Player10                                                           0.0       0000 - Z  0000 - Z  0000 - Z  0000 - Z  0000 - Z     9 b 1
                """, initialTrf);
    }

    @Test
    void testGenerate() {
        List<TrfLine> trf = TrfTournamentGenerator.runTournament(
                swissEngine,
                TrfTournamentGenerator.initTournament(15, 10)
        );
        assertEquals("""
                XXR 10
                001    1      Player1                                                            4.0          8 w 0     7 b 1     6 w 0     9 b 1     3 w 0    12 b 1    11 w 0    10 w 0     5 b 1     4 w 0 \s
                001    2      Player2                                                            5.0          9 b 1    10 w 0    12 b 1    14 w 0     6 b 1     7 b 1    15 w 0    13 b 1     3 w 0    11 w 0 \s
                001    3      Player3                                                            5.0         10 w 0     9 b 1    13 w 0    11 w 0     1 b 1     4 w 0     7 b 1  0000 - U     2 b 1     5 w 0 \s
                001    4      Player4                                                            4.0         11 b 1    12 w 0    14 b 1    13 w 0     5 w 0     3 b 1     9 w 0     7 w 0  0000 - U     1 b 1 \s
                001    5      Player5                                                            4.0         12 w 0    11 b 1     7 w 0     8 w 0     4 b 1     9 w 0  0000 - U    14 b 1     1 w 0     3 b 1 \s
                001    6      Player6                                                            5.0         13 b 1    14 w 0     1 b 1    15 b 1     2 w 0    11 b 1    12 w 0     9 b 1     7 w 0     8 w 0 \s
                001    7      Player7                                                            5.0         14 w 0     1 w 0     5 b 1  0000 - U     8 b 1     2 w 0     3 w 0     4 b 1     6 b 1    15 w 0 \s
                001    8      Player8                                                            5.0          1 b 1    15 w 0     9 w 0     5 b 1     7 w 0  0000 - U    10 b 1    12 b 1    13 w 0     6 b 1 \s
                001    9      Player9                                                            5.0          2 w 0     3 w 0     8 b 1     1 w 0  0000 - U     5 b 1     4 b 1     6 w 0    10 b 1    13 b 1 \s
                001   10      Player10                                                           4.0          3 b 1     2 b 1    15 w 0    12 w 0    13 b 1    14 w 0     8 w 0     1 b 1     9 w 0  0000 - U \s
                001   11      Player11                                                           5.0          4 w 0     5 w 0  0000 - U     3 b 1    14 b 1     6 w 0     1 b 1    15 b 1    12 w 0     2 b 1 \s
                001   12      Player12                                                           5.0          5 b 1     4 b 1     2 w 0    10 b 1    15 w 0     1 w 0     6 b 1     8 w 0    11 b 1    14 w 0 \s
                001   13      Player13                                                           6.0          6 w 0  0000 - U     3 b 1     4 b 1    10 w 0    15 b 1    14 b 1     2 w 0     8 b 1     9 w 0 \s
                001   14      Player14                                                           5.0          7 b 1     6 b 1     4 w 0     2 b 1    11 w 0    10 b 1    13 w 0     5 w 0    15 b 1    12 b 1 \s
                001   15      Player15                                                           5.0       0000 - U     8 b 1    10 b 1     6 w 0    12 b 1    13 w 0     2 b 1    11 w 0    14 w 0     7 b 1 \s
                """, TrfUtil.writeTrfLines(trf));
    }
}
