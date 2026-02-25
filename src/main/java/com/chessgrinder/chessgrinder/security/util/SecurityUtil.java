package com.chessgrinder.chessgrinder.security.util;

import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.security.principal.AuthorizedUserEntityProvider;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

public final class SecurityUtil {

    public static boolean hasRole(@Nullable UserEntity user, String role) {
        if (user == null || user.getRoles() == null || role == null || role.isBlank()) {
            return false;
        }

        return user.getRoles().stream().anyMatch(it -> it.getName().equalsIgnoreCase(role));
    }

    @Nullable
    public static UserEntity getCurrentUser() {
        SecurityContextHolder.getContext().getAuthentication();
        return tryGetUserEntity(SecurityContextHolder.getContext().getAuthentication());
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

    @Nonnull
    public static List<GrantedAuthority> getGrantedAuthorities(@Nullable UserEntity user){
        if (user == null || user.getRoles() == null) {return List.of();}
        return user.getRoles().stream()
                .map(RoleEntity::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private SecurityUtil() {
    }

}
