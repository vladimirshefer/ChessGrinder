package com.chessgrinder.chessgrinder.mappers;

import java.util.*;
import java.util.stream.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final BadgeRepository badgeRepository;
    private final BadgeMapper badgeMapper;
    public MemberDto toDto(User user) {

        List<Badge> userBadges = badgeRepository.getAllBadgesByUserId(user.getId());

        if (userBadges == null || userBadges.isEmpty()) {
            return MemberDto.builder()
                    .id(user.getId().toString())
                    .name(user.getName())
                    .build();
        }

        return MemberDto.builder()
                .id(user.getId().toString())
                .badges(badgeMapper.toDto(userBadges))
                .name(user.getName())
                .build();
    }

    public List<MemberDto> toDto(List<User> users) {
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


}
