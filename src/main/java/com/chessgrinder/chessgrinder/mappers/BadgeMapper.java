package com.chessgrinder.chessgrinder.mappers;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class BadgeMapper {

    public BadgeDto toDto(BadgeEntity badgeEntity) {

        return BadgeDto.builder()
                .title(badgeEntity.getTitle())
                .description(badgeEntity.getDescription())
                .imageUrl(badgeEntity.getPictureUrl())
                .build();
    }
    public List<BadgeDto> toDto(List<BadgeEntity> badgeEntities) {

        return badgeEntities.stream().map(badge -> BadgeDto.builder()
                .title(badge.getTitle())
                .description(badge.getDescription())
                .imageUrl(badge.getPictureUrl())
                .build()
        ).toList();
    }
}
