package com.chessgrinder.chessgrinder.mappers;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class BadgeMapper {

    public BadgeDto toDto(Badge badge) {

        return BadgeDto.builder()
                .title(badge.getTitle())
                .description(badge.getDescription())
                .imageUrl(badge.getPictureUrl())
                .build();
    }
    public List<BadgeDto> toDto(List<Badge> badges) {

        return badges.stream().map(badge -> BadgeDto.builder()
                .title(badge.getTitle())
                .description(badge.getDescription())
                .imageUrl(badge.getPictureUrl())
                .build()
        ).toList();
    }
}
