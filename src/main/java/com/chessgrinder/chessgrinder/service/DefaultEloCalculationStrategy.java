package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.EloUpdateResultDto;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import org.springframework.stereotype.Component;


public class DefaultEloCalculationStrategy implements EloCalculationStrategy {

    private static final int WIN_POINTS = 10;
    private static final int LOSE_POINTS = -10;
    private static final int UNRATED_WIN_POINTS = 5;
    private static final int UNRATED_LOSE_POINTS = -5;

    @Override
    public EloUpdateResultDto calculateElo(int whiteElo, int blackElo, MatchResult result, boolean bothUsersAuthorized) {

        if (result == null) {
            return EloUpdateResultDto.builder()
                    .whiteNewElo(whiteElo)
                    .blackNewElo(blackElo)
                    .build();
        }
        int whitePoints = 0;
        int blackPoints = 0;

        int winPoints = bothUsersAuthorized ? WIN_POINTS : UNRATED_WIN_POINTS;
        int losePoints = bothUsersAuthorized ? LOSE_POINTS : UNRATED_LOSE_POINTS;

        if (result == MatchResult.WHITE_WIN) {
            whitePoints = winPoints;
            blackPoints = losePoints;
        } else if (result == MatchResult.BLACK_WIN) {
            whitePoints = losePoints;
            blackPoints = winPoints;
        }

        int whiteNewElo = whiteElo + whitePoints;
        int blackNewElo = blackElo + blackPoints;

        return EloUpdateResultDto.builder()
                .whiteNewElo(whiteNewElo)
                .blackNewElo(blackNewElo)
                .build();
    }
}
