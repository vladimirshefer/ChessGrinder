package com.chessgrinder.chessgrinder.security;

import com.chessgrinder.chessgrinder.entities.UserEntity;

public final class SecurityUtil {

    public static boolean hasRole(UserEntity user, String role) {
        return user.getRoles().stream().anyMatch(it -> it.getName().equalsIgnoreCase(role));
    }

    private SecurityUtil() {
    }
}
