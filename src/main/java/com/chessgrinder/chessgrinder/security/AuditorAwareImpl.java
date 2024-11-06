package com.chessgrinder.chessgrinder.security;

import com.chessgrinder.chessgrinder.security.util.SecurityUtil;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

/**
 * Support for updated_at and created_at columns in database tables.
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @Nonnull
    public Optional<String> getCurrentAuditor() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        {
            var user = SecurityUtil.tryGetUserEntity(authentication);

            if (user != null) {
                if (user.getUsername() != null) {
                    return Optional.of(user.getUsername());
                }
            }
        }

        if (authentication.getPrincipal() instanceof OAuth2User principal) {
            final var attr = principal.getAttributes();
            Object email = attr.get("email");
            if (email != null) {
                return Optional.of(email.toString());
            }
        }

        return Optional.ofNullable(authentication.getName());
    }

}
