package com.chessgrinder.chessgrinder.dto;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSignUpRequest {
    private String username;
    private String fullName;
    @Nullable
    private String email;
    private String password;
}
