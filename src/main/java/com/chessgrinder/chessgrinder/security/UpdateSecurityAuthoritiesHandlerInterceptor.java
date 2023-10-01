package com.chessgrinder.chessgrinder.security;

import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashSet;

/**
 * Per each request goes to database extracting up-to-date user roles and updates current authentication.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateSecurityAuthoritiesHandlerInterceptor implements HandlerInterceptor {

    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        try {
            refreshAuthentication();
        } catch (Exception e) {
            log.error("Could not refresh user authentication", e);
        }
        return true;
    }

    private void refreshAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return;
        }

        var authorities = new HashSet<GrantedAuthority>();
        if (auth.isAuthenticated()) {
            authorities.add(new SimpleGrantedAuthority(RoleEntity.Roles.USER));
        }

        UserEntity userFromDatabase = userRepository.findByUsername(auth.getName());
        if (userFromDatabase != null) {
            authorities.addAll(userFromDatabase.getRoles().stream()
                    .map(RoleEntity::getName)
                    .map(SimpleGrantedAuthority::new)
                    .toList()
            );
        }

        Authentication newAuth = auth;

        if (auth instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            OAuth2User principal = oAuth2AuthenticationToken.getPrincipal();
            if (principal != null) {
                newAuth = new OAuth2AuthenticationToken(
                        principal,
                        authorities,
                        oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
                );
            }
        }

        log.trace("Refreshing user Authentication");

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

}
