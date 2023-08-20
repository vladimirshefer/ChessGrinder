package com.chessgrinder.chessgrinder.dto;

import com.chessgrinder.chessgrinder.enums.*;
import lombok.*;

@Data
@Builder
public class MatchDto {

    private UserDto user1;
    private UserDto user2;
    private MatchResult result;
}
