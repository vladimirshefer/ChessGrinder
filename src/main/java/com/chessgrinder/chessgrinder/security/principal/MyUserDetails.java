package com.chessgrinder.chessgrinder.security.principal;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@RequiredArgsConstructor
public class MyUserDetails implements UserDetails, AuthorizedUserEntityProvider {

    private final UserEntity userEntity;

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return SecurityUtil.getGrantedAuthorities(userEntity);
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public UserEntity getUserEntity() {
        return userEntity;
    }

}
