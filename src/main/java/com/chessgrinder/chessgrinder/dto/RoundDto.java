package com.chessgrinder.chessgrinder.dto;

import java.util.*;

import lombok.*;

@Data
@Builder
public class RoundDto {

    private Integer number;
    private List<MatchDto> matches;
    private boolean isFinished;
}
