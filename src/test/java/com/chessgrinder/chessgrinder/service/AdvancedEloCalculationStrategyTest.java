package com.chessgrinder.chessgrinder.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;


public class AdvancedEloCalculationStrategyTest {


    @Test
    public void testCalculateExpectedScore() {

        int whiteElo = 1600;
        int blackElo = 1600;
        double expectedScore = AdvancedEloCalculationStrategy.calculateExpectedScore(whiteElo, blackElo);
        assertEquals(0.5, expectedScore, 0.01);

        whiteElo = 2000;
        blackElo = 1600;
        expectedScore = AdvancedEloCalculationStrategy.calculateExpectedScore(whiteElo, blackElo);
        assertEquals(0.91, expectedScore, 0.01);

        whiteElo = 1600;
        blackElo = 2000;
        expectedScore = AdvancedEloCalculationStrategy.calculateExpectedScore(whiteElo, blackElo);
        assertEquals(0.09, expectedScore, 0.01);
    }


    @Test
    public void testCalculateNewElo() {

        //same Elo scenario
        int whiteElo = 1600;
        int blackElo = 1600;
        double score = 1; // white won

        int whiteNewElo = AdvancedEloCalculationStrategy.calculateNewElo(whiteElo, blackElo, score);
        int blackNewElo = AdvancedEloCalculationStrategy.calculateNewElo(blackElo, whiteElo, 0); // black lost

        assertEquals(1616, whiteNewElo);
        assertEquals(1584, blackNewElo);

        // High-rated player won
        whiteElo = 2000;
        blackElo = 1600;
        score = 1; // white won

        whiteNewElo = AdvancedEloCalculationStrategy.calculateNewElo(whiteElo, blackElo, score);
        blackNewElo = AdvancedEloCalculationStrategy.calculateNewElo(blackElo, whiteElo, 0); // black lost

        assertEquals(2003, whiteNewElo);
        assertEquals(1597, blackNewElo);

        // low-rated player won
        score = 1; // black won
        blackNewElo = AdvancedEloCalculationStrategy.calculateNewElo(blackElo, whiteElo, score);
        whiteNewElo = AdvancedEloCalculationStrategy.calculateNewElo(whiteElo, blackElo, 0); // white lost

        assertEquals(1629, blackNewElo);
        assertEquals(1971, whiteNewElo);

        // Draw scenario for big elo diff
        whiteElo = 2000;
        blackElo = 1400;
        score = 0.5; // draw
        whiteNewElo = AdvancedEloCalculationStrategy.calculateNewElo(whiteElo, blackElo, score);
        blackNewElo = AdvancedEloCalculationStrategy.calculateNewElo(blackElo, whiteElo, score);

        assertEquals(1985, whiteNewElo);
        assertEquals(1415, blackNewElo);
    }
}
