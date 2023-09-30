package com.chessgrinder.chessgrinder.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

@RequiredArgsConstructor
public class Oauth2AuthenticationWrapper implements Authentication {

    private final Authentication delegate;
    private final CustomOAuth2User customOAuth2User;

    @Override
    public String getName() {
        return customOAuth2User.getName();
    }

    @Override
    public boolean implies(Subject subject) {
        return delegate.implies(subject);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return customOAuth2User.getAuthorities();
    }

    @Override
    public Object getCredentials() {
        return delegate.getCredentials();
    }

    @Override
    public Object getDetails() {
        return delegate.getDetails();
    }

    @Override
    public Object getPrincipal() {
        return delegate.getPrincipal();
    }

    @Override
    public boolean isAuthenticated() {
        return delegate.isAuthenticated();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        delegate.setAuthenticated(isAuthenticated);
    }
}
