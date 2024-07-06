package com.chessgrinder.chessgrinder.enums;

public enum MatchResult {
    WHITE_WIN, BLACK_WIN, DRAW,
    /**
     * Equals to full point bye
     */
    BUY,
    /**
     * Equals to zero point bye.
     * If player is missing or disqualified
     */
    MISS
}
