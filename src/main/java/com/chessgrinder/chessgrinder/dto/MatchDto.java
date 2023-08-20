package com.chessgrinder.chessgrinder.dto;

import com.chessgrinder.chessgrinder.enums.*;
import lombok.*;

@Data
@Builder
public class MatchDto {

    private MemberDto white;
    private MemberDto black;
    private MatchResult result;
}
