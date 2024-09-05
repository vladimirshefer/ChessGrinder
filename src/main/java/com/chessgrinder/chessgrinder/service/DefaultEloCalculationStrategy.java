package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.EloUpdateResultDto;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import org.springframework.stereotype.Component;

@Component
public class DefaultEloCalculationStrategy implements EloCalculationStrategy {

    private static final int WIN_POINTS = 10;
    private static final int LOSE_POINTS = -10;
    private static final int UNRATED_WIN_POINTS = 5;
    private static final int UNRATED_LOSE_POINTS = -5;

    @Override
    public EloUpdateResultDto calculateElo(int playerElo, int opponentElo, MatchResult result, boolean bothUsersAuthorized) {
        int playerPoints = 0;
        int opponentPoints = 0;

        // Определяем, какое значение использовать для расчета
        int winPoints = bothUsersAuthorized ? WIN_POINTS : UNRATED_WIN_POINTS;
        int losePoints = bothUsersAuthorized ? LOSE_POINTS : UNRATED_LOSE_POINTS;

        switch (result) {
            case WHITE_WIN:
                playerPoints = winPoints;
                opponentPoints = losePoints;
                break;
            case BLACK_WIN:
                playerPoints = losePoints;
                opponentPoints = winPoints;
                break;
            case DRAW:
                // Если ничья, изменения не происходит
                break;
            default:
                // Если результат не определен, изменений не происходит
                break;
        }

        int playerNewElo = playerElo + playerPoints;
        int opponentNewElo = opponentElo + opponentPoints;

        return EloUpdateResultDto.builder()
                .playerNewElo(playerNewElo)
                .opponentNewElo(opponentNewElo)
                .build();
    }
}
