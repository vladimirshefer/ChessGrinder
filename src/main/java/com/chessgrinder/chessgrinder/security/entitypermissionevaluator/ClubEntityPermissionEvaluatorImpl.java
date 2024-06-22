package com.chessgrinder.chessgrinder.security.entitypermissionevaluator;

import com.chessgrinder.chessgrinder.entities.ClubEntity;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubEntityPermissionEvaluatorImpl implements EntityPermissionEvaluator<ClubEntity> {

    private final UserRoleRepository userRoleRepository;

    @Override
    public boolean hasPermission(UUID userId, ClubEntity entity, String permission) {
        return hasPermission(userId, entity.getId(), permission);
    }

    @Override
    public boolean hasPermission(UUID userId, String entityId, String permission) {
        UUID clubId = UUID.fromString(entityId);
        return hasPermission(userId, clubId, permission);
    }

    private boolean hasPermission(UUID userId, UUID clubId, String permission) {
        List<RoleEntity> roles = userRoleRepository.getRolesByUserIdAndClubId(userId, clubId);
        return roles.stream().map(RoleEntity::getName).anyMatch(it -> it.equalsIgnoreCase(permission.trim()));
    }

}
