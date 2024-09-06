package com.chessgrinder.chessgrinder.dto;

import com.chessgrinder.chessgrinder.enums.MatchResult;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubmitMatchResultRequestDto {
    @Nullable
    MatchResult matchResult;
}
