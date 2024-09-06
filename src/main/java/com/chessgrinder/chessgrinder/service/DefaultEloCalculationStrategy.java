package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.EloUpdateResultDto;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DefaultEloCalculationStrategy implements EloCalculationStrategy {

    private static final int WIN_POINTS = 10;
    private static final int LOSE_POINTS = -10;
    private static final int UNRATED_WIN_POINTS = 5;
    private static final int UNRATED_LOSE_POINTS = -5;

    @Override
    public EloUpdateResultDto calculateElo(int whiteElo, int blackElo, MatchResult result, boolean bothUsersAuthorized) {
        int playerPoints = 0;
        int opponentPoints = 0;

        int winPoints = bothUsersAuthorized ? WIN_POINTS : UNRATED_WIN_POINTS;
        int losePoints = bothUsersAuthorized ? LOSE_POINTS : UNRATED_LOSE_POINTS;

        if (Objects.requireNonNull(result) == MatchResult.WHITE_WIN) {
            playerPoints = winPoints;
            opponentPoints = losePoints;
        } else if (result == MatchResult.BLACK_WIN) {
            playerPoints = losePoints;
            opponentPoints = winPoints;
        }

        int playerNewElo = whiteElo + playerPoints;
        int opponentNewElo = blackElo + opponentPoints;

        return EloUpdateResultDto.builder()
                .playerNewElo(playerNewElo)
                .opponentNewElo(opponentNewElo)
                .build();
    }
}
