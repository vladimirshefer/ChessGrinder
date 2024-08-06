package com.chessgrinder.chessgrinder.dto;

import com.chessgrinder.chessgrinder.enums.*;
import jakarta.annotation.Nullable;
import lombok.*;

@Data
public class SubmitMatchResultRequestDto {

    @Nullable
    MatchResult matchResult;
}
