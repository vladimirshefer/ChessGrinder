package com.chessgrinder.chessgrinder.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EloUpdateResultDto {
    private final int playerNewElo;
    private final int opponentNewElo;
}
