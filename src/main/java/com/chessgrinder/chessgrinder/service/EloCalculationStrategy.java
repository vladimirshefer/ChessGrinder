package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.enums.MatchResult;

public interface EloCalculationStrategy {
    EloPair calculateElo(int whiteElo, int blackElo, MatchResult result, boolean  bothUsersAuthorized);

    record EloPair(int whiteElo, int blackElo) { }
}
