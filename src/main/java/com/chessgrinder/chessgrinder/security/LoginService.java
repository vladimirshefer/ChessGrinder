package com.chessgrinder.chessgrinder.security;

import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.principal.CustomOAuth2User;
import com.chessgrinder.chessgrinder.security.principal.CustomOidcUser;
import com.chessgrinder.chessgrinder.security.util.SecurityUtil;
import com.chessgrinder.chessgrinder.service.RoleService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;

    @Autowired
    public LoginService(
            UserRepository userRepository,
            RoleService roleService,
            @Value("${chessgrinder.security.adminEmail:}")
            String adminEmail
    ) {
        this.roleService = roleService;
        this.userRepository = userRepository;
        this.adminEmail = adminEmail;
        this.oAuth2UserService = new DefaultOAuth2UserService();
        this.oidcUserService = new OidcUserService();
    }

    private final String adminEmail;

    public OidcUser loadOidcUser(OidcUserRequest userRequest) {
        OidcUser principal = oidcUserService.loadUser(userRequest);
        String email = principal.getAttribute("email");
        String preferredUsertag = principal.getPreferredUsername();
        String fullName = principal.getFullName();
        if (fullName == null) {
            fullName = principal.getAttribute("name");
        }
        UserEntity.Provider provider = LoginService.mapProvider(userRequest.getClientRegistration().getRegistrationId());
        UserEntity userEntity = loginOrRegister(email, preferredUsertag, fullName, null, provider);
        return CustomOidcUser.builder().delegate(principal).user(userEntity).build();
    }

    public OAuth2User loadOauth2User(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User principal = oAuth2UserService.loadUser(userRequest);
        String email = principal.getAttribute("email");
        String fullName = principal.getAttribute("name");
        UserEntity.Provider provider = LoginService.mapProvider(userRequest.getClientRegistration().getRegistrationId());
        UserEntity userEntity = loginOrRegister(email, null, fullName, null, provider);
        return CustomOAuth2User.builder().delegate(principal).user(userEntity).build();
    }

    public UserEntity loginOrRegister(
            @Nonnull
            String username,
            @Nullable
            String preferredUsertag,
            @Nullable
            String fullName,
            @Nullable
            String password,
            @Nullable
            UserEntity.Provider provider
    ) {
        Objects.requireNonNull(username, "Username or Email cannot be null");
        UserEntity user = userRepository.findByUsername(username);
        UserEntity byUserTag = null;
        if (preferredUsertag != null) {
            byUserTag = userRepository.findByUsertag(preferredUsertag);
        }
        if (byUserTag != null && !Objects.equals(byUserTag.getId(), user.getId())) {
            preferredUsertag = null;
        }
        if (preferredUsertag != null && !preferredUsertag.matches(UserEntity.USERTAG_REGEX)) {
            preferredUsertag = null;
        }
        if (user != null) {
            if (StringUtils.isBlank(user.getUsertag()) && StringUtils.isNotBlank(preferredUsertag)) {
                user.setUsertag(preferredUsertag);
                user = userRepository.save(user);
            }
            if (user.getProvider() == null && provider != null) {
                user.setProvider(provider);
                user = userRepository.save(user);
            }
            if (StringUtils.isBlank(user.getName()) && StringUtils.isNotBlank(fullName)) {
                user.setName(fullName);
                user = userRepository.save(user);
            }
            if (StringUtils.isBlank(user.getName()) && StringUtils.isNotBlank(preferredUsertag)) {
                user.setName(preferredUsertag);
            }
        }
        if (user == null) {
            user = registerNewUser(username, fullName, provider, preferredUsertag);
        }
        user = setAdminOnLogin(username, user);
        return user;
    }

    public static UserEntity.Provider mapProvider(String provider) {
        if (provider.equals("google")) {
            return UserEntity.Provider.GOOGLE;
        }
        if (provider.equals("chesscom")) {
            return UserEntity.Provider.CHESSCOM;
        }
        return null;
    }

    private UserEntity registerNewUser(
            String email,
            String fullName,
            UserEntity.Provider provider,
            String preferredUsertag
    ) {
        UserEntity newUser = new UserEntity();
        newUser.setUsername(email);
        newUser.setName(fullName);
        newUser.setProvider(provider);
        newUser.setUsertag(preferredUsertag);
        return userRepository.save(newUser);
    }

    private UserEntity setAdminOnLogin(String email, UserEntity user) {
        Set<String> adminEmails = Arrays.stream(adminEmail.split(","))
                .filter(StringUtils::isNotBlank)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (adminEmails.contains(email)) {
            if (!SecurityUtil.hasRole(user, RoleEntity.Roles.ADMIN)) {
                roleService.assignRole(user, RoleEntity.Roles.ADMIN);
            }

            user = userRepository.findById(user.getId()).orElseThrow();
        }
        return user;
    }

}
