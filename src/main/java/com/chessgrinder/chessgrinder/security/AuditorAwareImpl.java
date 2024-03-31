package com.chessgrinder.chessgrinder.security;

import jakarta.annotation.Nonnull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @Nonnull
    public Optional<String> getCurrentAuditor() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof CustomOAuth2User principal)) {
            return Optional.empty();
        }
        final var user = principal.getUser();
        if (user == null || user.getUsername() == null) {
            //if user just created
            final var attr = principal.getAttributes();
            return attr.containsKey("email") ? Optional.of(attr.get("email").toString()) : Optional.empty();
        }
        return Optional.of(user.getUsername());
    }
}
