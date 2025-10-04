package com.chessgrinder.chessgrinder.security.entitypermissionevaluator;

import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.util.SecurityUtil;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TournamentEntityPermissionEvaluatorImpl implements EntityPermissionEvaluator<TournamentEntity> {
    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final ParticipantRepository participantRepository;

    @Override
    public boolean hasPermission(@Nullable UUID userId, @Nullable String entityId, @Nullable String permission) {
        if (userId == null || StringUtils.isBlank(entityId) || StringUtils.isBlank(permission)) return false;

        UUID tournamentId;
        try {
            tournamentId = UUID.fromString(entityId);
        } catch (IllegalArgumentException e) {
            return false;
        }

        if (!tournamentRepository.existsById(tournamentId)) {
            return false;
        }

        UserEntity userEntity = userRepository.findById(userId).orElse(null);

        if (userEntity == null) return false;

        ParticipantEntity byTournamentIdAndUserId = participantRepository.findByTournamentIdAndUserId(tournamentId, userId);

        if (SecurityUtil.hasRole(userEntity, RoleEntity.Roles.ADMIN)) {
            return true;
        }

        if (byTournamentIdAndUserId != null) {
            if (Objects.equals(permission, Permissions.MODERATOR.name())) {
                if (byTournamentIdAndUserId.isModerator()) {
                    return true;
                }
            }
        }

        return false;
    }

    public enum Permissions {
        OWNER, MODERATOR
    }

}
