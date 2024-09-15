package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.enums.MatchResult;
import org.junit.jupiter.api.Test;

import static com.chessgrinder.chessgrinder.service.AdvancedEloCalculationStrategy.calculateExpectedScore;
import static com.chessgrinder.chessgrinder.service.AdvancedEloCalculationStrategy.calculateNewElo;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class AdvancedEloCalculationStrategyTest {

    @Test
    public void testCalculateExpectedScore() {
        assertEquals(0.5, calculateExpectedScore(1600, 1600), 0.01);
        assertEquals(0.91, calculateExpectedScore(2000, 1600), 0.01);
        assertEquals(0.09, calculateExpectedScore(1600, 2000), 0.01);
    }

    @Test
    public void testCalculateNewElo() {
        // Equal Elo scenario
        assertEquals(1616, calculateNewElo(1600, 1600, 1 /* white won */));
        assertEquals(1584, calculateNewElo(1600, 1600, 0 /* black lost */));

        // High-rated player won
        assertEquals(2003, calculateNewElo(2000, 1600, 1 /* white won */));
        assertEquals(1597, calculateNewElo(1600, 2000, 0 /* black lost */));

        // Low-rated player won
        assertEquals(1629, calculateNewElo(1600, 2000, 1 /* black won */));
        assertEquals(1971, calculateNewElo(2000, 1600, 0 /* white lost */));

        // Draw scenario for big elo diff
        assertEquals(1985, calculateNewElo(2000, 1400, 0.5));
        assertEquals(1415, calculateNewElo(1400, 2000, 0.5));
    }

    @Test
    void testCalculateElo() {
        {
            var result = new AdvancedEloCalculationStrategy().calculateElo(2000, 2000, MatchResult.WHITE_WIN, true);
            assertEquals(2016, result.whiteElo());
            assertEquals(1984, result.blackElo());
        }
        {
            var result = new AdvancedEloCalculationStrategy().calculateElo(2000, 1000, MatchResult.WHITE_WIN, true);
            assertEquals(2000, result.whiteElo());
            assertEquals(1000, result.blackElo());
        }
        {
            var result = new AdvancedEloCalculationStrategy().calculateElo(1000, 2000, MatchResult.WHITE_WIN, true);
            assertEquals(1032, result.whiteElo());
            assertEquals(1968, result.blackElo());
        }
    }
}
