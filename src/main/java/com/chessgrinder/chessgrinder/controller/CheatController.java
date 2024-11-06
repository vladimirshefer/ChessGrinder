package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.entities.UserRoleEntity;
import com.chessgrinder.chessgrinder.repositories.RoleRepository;
import com.chessgrinder.chessgrinder.repositories.UserRoleRepository;
import com.chessgrinder.chessgrinder.security.AuthenticatedUserArgumentResolver.AuthenticatedUser;
import com.chessgrinder.chessgrinder.security.util.SecurityUtil;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Cheat controller is used in testing environment to allow developers modify server state.
 * This should NEVER be enabled in production.
 */
@ConditionalOnProperty(value = "com.chessgrinder.cheats.enabled", havingValue = "true")
@RestController
@RequestMapping("/cheat")
@RequiredArgsConstructor
public class CheatController {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @GetMapping("/getAdminRole")
    public Object getAdminRole(
            @AuthenticatedUser(required = false)
            @Nullable
            UserEntity user
    ) {
        if (user == null) {
            throw new ResponseStatusException(401, "Please log in", null);
        }

        if (SecurityUtil.hasRole(user, RoleEntity.Roles.ADMIN)) {
            return "User already has ADMIN authority";
        }

        RoleEntity adminRole = roleRepository.findByName(RoleEntity.Roles.ADMIN)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder().name(RoleEntity.Roles.ADMIN).build()));

        userRoleRepository.save(UserRoleEntity.builder()
                .user(user)
                .role(adminRole)
                .build()
        );

        return "Admin role granted";
    }

}
