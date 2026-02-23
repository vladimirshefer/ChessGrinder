package com.chessgrinder.chessgrinder.mappers;

import com.chessgrinder.chessgrinder.dto.UserDto;
import com.chessgrinder.chessgrinder.entities.BadgeEntity;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.repositories.BadgeRepository;
import com.chessgrinder.chessgrinder.security.util.SecurityUtil;
import com.chessgrinder.chessgrinder.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final BadgeRepository badgeRepository;
    private final BadgeMapper badgeMapper;

    public UserDto toDto(
            UserEntity user
    ) {
        List<BadgeEntity> userBadges = badgeRepository.getAllBadgesByUserId(user.getId());
        if (userBadges == null) {
            userBadges = Collections.emptyList();
        }

        boolean isAdmin = SecurityUtil.hasRole(SecurityUtil.getCurrentUser(), RoleEntity.Roles.ADMIN);

        return UserDto.builder()
                .id(user.getId().toString())
                .username(isAdmin ? user.getUsername() : null)
                .usertag(user.getUsertag())
                // email has is taken from username field
                .emailHash(HashUtil.getMd5Hash(user.getUsername()))
                .badges(badgeMapper.toDto(userBadges))
                .name(user.getName())
                .roles(user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toList()))
                .reputation(user.getReputation())
                .eloPoints(user.getEloPoints())
                .build();
    }

    public List<UserDto> toDto(List<UserEntity> users) {
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}
