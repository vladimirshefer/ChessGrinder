package com.chessgrinder.chessgrinder.security;

import com.chessgrinder.chessgrinder.security.CustomOAuth2User;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    @Nonnull
    public Optional<String> getCurrentAuditor() {
        //TODO https://www.youtube.com/watch?v=1D5zEzLX1iY Засунуть тесты!
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        var user = ((CustomOAuth2User) (authentication.getPrincipal())).getUser();
        assert Objects.requireNonNull(user).getUsername() != null;
        return Optional.of(user.getUsername());
    }
}
