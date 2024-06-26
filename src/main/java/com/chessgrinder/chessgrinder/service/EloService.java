package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.*;
import org.springframework.stereotype.*;

@Component
public class EloService {

    private static final double K_FACTOR = 32;

    public static double calculateExpectedScore(int playerRating, int opponentRating) {
        return 1.0 / (1 + Math.pow(10, (opponentRating - playerRating) / 400.0));
    }

    public static int calculateNewRating(int playerRating, int opponentRating, double score) {
        double expectedScore = calculateExpectedScore(playerRating, opponentRating);
        return (int) Math.round(playerRating + K_FACTOR * (score - expectedScore));
    }

    public void processMatchResult(MatchEntity match) {
        ParticipantEntity participant1 = match.getParticipant1();
        ParticipantEntity participant2 = match.getParticipant2();

        int eloPointsFirst = participant1.getUser().getEloPoints();
        int eloPointsSecond = participant2.getUser().getEloPoints();

        if (match.getResult().equals(MatchResult.WHITE_WIN)) {
            int newRatingFirst = calculateNewRating(eloPointsFirst, eloPointsSecond, 1.0);
            int newRatingSecond = calculateNewRating(eloPointsSecond, eloPointsFirst, 0.0);
            eloPointsFirst = newRatingFirst;
            eloPointsSecond = newRatingSecond;
        } else if (match.getResult().equals(MatchResult.BLACK_WIN)) {
            int newRatingFirst = calculateNewRating(eloPointsFirst, eloPointsSecond, 0.0);
            int newRatingSecond = calculateNewRating(eloPointsSecond, eloPointsFirst, 1.0);
            eloPointsFirst = newRatingFirst;
            eloPointsSecond = newRatingSecond;
        } else if (match.getResult().equals(MatchResult.DRAW)) {
            int newRatingFirst = calculateNewRating(eloPointsFirst, eloPointsSecond, 0.5);
            int newRatingSecond = calculateNewRating(eloPointsSecond, eloPointsFirst, 0.5);
            eloPointsFirst = newRatingFirst;
            eloPointsSecond = newRatingSecond;
        }

        participant1.getUser().setEloPoints(eloPointsFirst);
        participant2.getUser().setEloPoints(eloPointsSecond);
    }
}
