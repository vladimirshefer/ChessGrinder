package com.chessgrinder.chessgrinder.mappers;

import java.util.*;
import java.util.stream.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.repositories.*;
import com.chessgrinder.chessgrinder.util.HashUtil;
import lombok.*;
import org.springframework.stereotype.*;

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

        return UserDto.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                // email has is taken from username field
                .emailHash(HashUtil.getMd5Hash(user.getUsername()))
                .badges(badgeMapper.toDto(userBadges))
                .name(user.getName())
                .roles(user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toList()))
                .reputation(user.getReputation())
                .globalScore(user.getGlobalScore())
                .build();
    }

    public List<UserDto> toDto(List<UserEntity> users) {
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}
