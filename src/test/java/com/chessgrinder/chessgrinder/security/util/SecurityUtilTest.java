package com.chessgrinder.chessgrinder.security.util;

import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityUtilTest {

    @Test
    void hasRole_shouldBeTrue_forModeratorRole() {
        assertTrue(SecurityUtil.hasRole(userWithRoles(RoleEntity.Roles.USER, RoleEntity.Roles.MODERATOR), RoleEntity.Roles.MODERATOR));
    }

    @Test
    void hasRole_shouldBeCaseInsensitive() {
        assertTrue(SecurityUtil.hasRole(userWithRoles("role_moderator"), RoleEntity.Roles.MODERATOR));
    }

    @Test
    void hasRole_shouldBeFalse_forOtherRoleNoRolesNullRolesAndNullUser() {
        assertFalse(SecurityUtil.hasRole(userWithRoles(RoleEntity.Roles.USER), RoleEntity.Roles.MODERATOR));
        assertFalse(SecurityUtil.hasRole(userWithRoles(), RoleEntity.Roles.MODERATOR));
        assertFalse(SecurityUtil.hasRole(new UserEntity(), RoleEntity.Roles.MODERATOR));
        assertFalse(SecurityUtil.hasRole(null, RoleEntity.Roles.MODERATOR));
    }

    private static UserEntity userWithRoles(String... roles) {
        UserEntity user = new UserEntity();
        user.setRoles(Arrays.stream(roles).map(it -> RoleEntity.builder().name(it).build()).toList());
        return user;
    }
}
