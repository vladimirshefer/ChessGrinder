package com.chessgrinder.chessgrinder.security;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import jakarta.annotation.Nullable;
import org.springframework.security.core.Authentication;

public final class SecurityUtil {

    @Nullable
    public static UserEntity tryGetUserEntity(Authentication authentication) {
        // Not sure if all these combinations are possible and required. Just do everything to guess.
        if (authentication instanceof UserEntity userEntity) {
            return userEntity;
        }
        if (authentication instanceof AuthorizedUserEntityProvider authorizedUserEntityProvider) {
            return authorizedUserEntityProvider.getUserEntity();
        }
        if (authentication.getPrincipal() instanceof UserEntity userEntity) {
            return userEntity;
        }
        if (authentication.getPrincipal() instanceof AuthorizedUserEntityProvider authorizedUserEntityProvider) {
            return authorizedUserEntityProvider.getUserEntity();
        }
        return null;
    }

    private SecurityUtil(){
    }

}
