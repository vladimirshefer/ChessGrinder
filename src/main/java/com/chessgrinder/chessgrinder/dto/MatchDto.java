package com.chessgrinder.chessgrinder.dto;

import com.chessgrinder.chessgrinder.enums.MatchResult;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchDto {
    private String id;
    @Nullable
    private ParticipantDto white;
    @Nullable
    private ParticipantDto black;
    @Nullable
    private MatchResult result;
    @Nullable
    private MatchResult resultSubmittedByWhite;
    @Nullable
    private MatchResult resultSubmittedByBlack;
}
