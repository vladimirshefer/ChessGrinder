package com.chessgrinder.chessgrinder.dto;

import com.chessgrinder.chessgrinder.enums.*;
import lombok.*;

@Data
@Builder
public class MatchDto {
    private String id;
    private ParticipantDto white;
    private ParticipantDto black;
    private MatchResult result;
}
