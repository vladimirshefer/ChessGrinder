package com.chessgrinder.chessgrinder.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatsAgainstUserDTO {

    private int wins;

    private int losses;

    private int draws;
}
