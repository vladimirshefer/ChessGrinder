package com.chessgrinder.chessgrinder.dto;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
public class RoundDto {

    private Integer number;
    private List<MatchDto> matches;
    @JsonProperty("isFinished")
    private boolean isFinished;
}
