package com.chessgrinder.chessgrinder.chessengine.ratings;

import com.chessgrinder.chessgrinder.enums.MatchResult;


public class FixedEloCalculationStrategy implements EloCalculationStrategy {

    private static final int WIN_POINTS = 10;
    private static final int LOSE_POINTS = -10;
    private static final int UNRATED_WIN_POINTS = 5;
    private static final int UNRATED_LOSE_POINTS = -5;

    @Override
    public EloPair calculateElo(int whiteElo, int blackElo, MatchResult result, boolean bothUsersAuthorized) {
        if (result == null) {
            return new EloPair(whiteElo, blackElo);
        }

        int winPoints = bothUsersAuthorized ? WIN_POINTS : UNRATED_WIN_POINTS;
        int losePoints = bothUsersAuthorized ? LOSE_POINTS : UNRATED_LOSE_POINTS;

        if (result == MatchResult.WHITE_WIN) {
            return new EloPair(whiteElo + winPoints, blackElo + losePoints);
        }

        if (result == MatchResult.BLACK_WIN) {
            return new EloPair(whiteElo + losePoints, blackElo + winPoints);
        }

        return new EloPair(whiteElo, blackElo);
    }
}
