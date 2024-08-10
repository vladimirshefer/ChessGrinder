package com.chessgrinder.chessgrinder.dto;

import com.chessgrinder.chessgrinder.enums.*;
import jakarta.annotation.Nullable;
import lombok.*;

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
    private MatchResult resultSubmittedByParticipant1;
    @Nullable
    private MatchResult resultSubmittedByParticipant2;
}
