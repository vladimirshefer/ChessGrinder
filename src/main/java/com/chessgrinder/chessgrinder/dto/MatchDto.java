package com.chessgrinder.chessgrinder.dto;

import com.chessgrinder.chessgrinder.enums.*;
import lombok.*;

@Data
@Builder
public class MatchDto {

    private MemberDto user1;
    private MemberDto user2;
    private MatchResult result;
}
