package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.EloUpdateResultDto;
import com.chessgrinder.chessgrinder.enums.MatchResult;

public interface EloCalculationStrategy {
    EloUpdateResultDto calculateElo(int player1Elo, int player2Elo, MatchResult result, boolean  isAnyUserUnauthorized);
}
