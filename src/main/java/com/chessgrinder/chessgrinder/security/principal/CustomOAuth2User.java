package com.chessgrinder.chessgrinder.security.principal;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.security.util.SecurityUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public final class CustomOAuth2User implements OAuth2User, AuthorizedUserEntityProvider {

    @Nonnull
    private final OAuth2User delegate;
    @Nonnull
    private final UserEntity user;

    @Override
    public String getName() {
        return user.getUsername();
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        return SecurityUtil.getGrantedAuthorities(user);
    }

    @Override
    @Nullable
    public UserEntity getUserEntity() {
        return user;
    }

    /*
    =======================================
              DELEDATED METHODS
    =======================================
     */

    @Override
    public <A> A getAttribute(String name) {
        return delegate.getAttribute(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }
}
