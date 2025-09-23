package com.chessgrinder.chessgrinder.chessengine.pairings;

import com.chessgrinder.chessgrinder.chessengine.PairingFailedException;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.util.TrfUtil;
import com.chessgrinder.chessgrinder.testutil.trf.TrfTournamentGenerator;
import com.chessgrinder.chessgrinder.testutil.trf.TrfTournamentRunner;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RoundRobinPairingStrategyImplTest {

    PairingStrategy pairingStrategy = new RoundRobinPairingStrategyImpl();

    @Test
    void test3p() {
        List<TrfLine> trf = TrfTournamentGenerator.runTournament(pairingStrategy, TrfTournamentGenerator.initTournament(3, 10));
        TrfTournamentRunner.verify(pairingStrategy, trf);
        assertEquals("""
                XXR 10
                001    1      Player1                                                            6.0       0000 - U     2 w 0     3 b 1  0000 - U     2 w 0     3 b 1  0000 - U     2 w 0     3 b 1  0000 - U \s
                001    2      Player2                                                            6.0          3 w 0     1 b 1  0000 - U     3 w 0     1 b 1  0000 - U     3 w 0     1 b 1  0000 - U     3 w 0 \s
                001    3      Player3                                                            6.0          2 b 1  0000 - U     1 w 0     2 b 1  0000 - U     1 w 0     2 b 1  0000 - U     1 w 0     2 b 1 \s
                """, TrfUtil.writeTrfLines(trf));
    }

    @Test
    void test_8_participants_1_missing_repeated() {
        List<TrfLine> trf1 = TrfUtil.readTrf("""
                XXR 10
                XXZ 1
                001    5      player05                          1186                             0.0          6 w 1     7 b 1     8 w 1     2 b 1     3 w 1     4 b 1  0000 - U     6 w 1     7 b 1     8 w 1 \s
                001    6      player06                          1127                             0.0          5 b 0  0000 - U     7 w 0     8 b 0     2 w 0     3 b 0     4 w 0     5 b 0  0000 - U     7 w 0 \s
                001    7      player07                          1211                             0.0          4 b 0     5 w 0     6 b 0  0000 - U     8 w 0     2 b 0     3 w 0     4 b 0     5 w 0     6 b 0 \s
                001    8      player08                          1229                             0.0          3 b 0     4 w 0     5 b 0     6 w 0     7 b 0  0000 - U     2 w 0     3 b 0     4 w 0     5 b 0 \s
                001    1      player01                          1000                             0.0       0000 - Z  0000 - Z  0000 - Z  0000 - Z  0000 - Z  0000 - Z  0000 - Z  0000 - Z  0000 - Z  0000 - Z \s
                001    2      player02                          1200                             0.0       0000 - U     3 w 0     4 b 0     5 w 0     6 b U     7 w 1     8 b 1  0000 - U     3 w 0     4 b 0 \s
                001    3      player03                          1200                             0.0          8 w 1     2 b 1  0000 - U     4 w 1     5 b 1     6 w 1     7 b 1     8 w 1     2 b 1  0000 - U \s
                001    4      player04                          1161                             0.0          7 w 1     8 b 1     2 w 1     3 b 1  0000 - U     5 w 1     6 b 1     7 w 1     8 b 1     2 w 1 \s
                """);

        TrfTournamentRunner.verify(pairingStrategy, trf1);
    }

    /**
     * Tests that if a new player is added mid-tournament, then the pairing fails.
     */
    @Test
    void test3pAdd() {
        var trf = """
                XXR 10
                001    3      player03                                                           0.0       0000 - U
                001    1      player01                                                           0.0          3 w 1
                001    2      player02                                                           0.0          2 b 0
                001    4      player04                                                           0.0       0000 - Z
                """;
        assertThrows(PairingFailedException.class, () -> pairingStrategy.makePairings(TrfUtil.readTrf(trf)));
    }

    @Test
    void test6pAdd() {
        var trf = """
                XXR 10
                001    1      player01                                                           0.0          6 w ?
                001    2      player02                                                           0.0          5 w ?
                001    3      player03                                                           0.0          4 w ?
                001    4      player04                                                           0.0          3 b ?
                001    5      player05                                                           0.0          2 b ?
                001    6      player06                                                           0.0          1 b ?
                001    7      player07                                                           0.0       0000 - Z
                """;
        assertThrows(PairingFailedException.class, () -> pairingStrategy.makePairings(TrfUtil.readTrf(trf)));
    }

}
