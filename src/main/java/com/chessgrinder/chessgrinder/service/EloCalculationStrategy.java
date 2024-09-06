package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.EloUpdateResultDto;
import com.chessgrinder.chessgrinder.enums.MatchResult;

public interface EloCalculationStrategy {
    EloUpdateResultDto calculateElo(int whiteElo, int blackElo, MatchResult result, boolean  bothUsersAuthorized);
}
