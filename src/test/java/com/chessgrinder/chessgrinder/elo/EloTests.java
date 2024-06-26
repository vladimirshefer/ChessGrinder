package com.chessgrinder.chessgrinder.elo;

import static org.junit.jupiter.api.Assertions.*;

import com.chessgrinder.chessgrinder.service.*;
import org.junit.jupiter.api.*;

public class EloTests {

    @Test
    public void testCalculateExpectedScore() {

        int playerRating = 1600;
        int opponentRating = 1600;
        double expectedScore = EloService.calculateExpectedScore(playerRating, opponentRating);
        assertEquals(0.5, expectedScore, 0.01);

        playerRating = 2000;
        opponentRating = 1600;
        expectedScore = EloService.calculateExpectedScore(playerRating, opponentRating);
        assertEquals(0.91, expectedScore, 0.01);

        playerRating = 1600;
        opponentRating = 2000;
        expectedScore = EloService.calculateExpectedScore(playerRating, opponentRating);
        assertEquals(0.09, expectedScore, 0.01);
    }


    @Test
    public void testCalculateNewRating() {
        int playerRating = 1600;
        int opponentRating = 1600;
        double score = 1; // player won

        int newRating = EloService.calculateNewRating(playerRating, opponentRating, score);
        assertEquals(1616, newRating);

        score = 0; // player lost
        newRating = EloService.calculateNewRating(playerRating, opponentRating, score);
        assertEquals(1584, newRating);

        playerRating = 2000;
        opponentRating = 1600;
        score = 1; // player won

        newRating = EloService.calculateNewRating(playerRating, opponentRating, score);
        assertEquals(2002, newRating);

        score = 0; // player lost
        newRating = EloService.calculateNewRating(playerRating, opponentRating, score);
        assertEquals(1970, newRating);

        playerRating = 1600;
        opponentRating = 2000;
        score = 1; // player won
        newRating = EloService.calculateNewRating(playerRating, opponentRating, score);
        assertEquals(1629, newRating);

        score = 0; // player lost
        newRating = EloService.calculateNewRating(playerRating, opponentRating, score);
        assertEquals(1597, newRating);
    }
}
