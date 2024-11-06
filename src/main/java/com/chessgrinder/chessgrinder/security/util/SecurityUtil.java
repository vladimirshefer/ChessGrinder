package com.chessgrinder.chessgrinder.security.util;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.security.principal.AuthorizedUserEntityProvider;
import jakarta.annotation.Nullable;
import org.springframework.security.core.Authentication;

public final class SecurityUtil {

    public static boolean hasRole(UserEntity user, String role) {
        return user.getRoles().stream().anyMatch(it -> it.getName().equalsIgnoreCase(role));
    }

    @Nullable
    public static UserEntity tryGetUserEntity(@Nullable Authentication authentication) {
        if (authentication == null) {
            return null;
        }

        if (authentication instanceof UserEntity userEntity) {
            return userEntity;
        }

        if (authentication instanceof AuthorizedUserEntityProvider authorizedUserEntityProvider) {
            UserEntity userEntity = authorizedUserEntityProvider.getUserEntity();
            if (userEntity != null) {
                return userEntity;
            }
        }

        if (authentication.getPrincipal() instanceof UserEntity userEntity) {
            return userEntity;
        }

        if (authentication.getPrincipal() instanceof AuthorizedUserEntityProvider authorizedUserEntityProvider) {
            UserEntity userEntity = authorizedUserEntityProvider.getUserEntity();
            if (userEntity != null) {
                return userEntity;
            }
        }
        return null;
    }

    private SecurityUtil() {
    }

}
