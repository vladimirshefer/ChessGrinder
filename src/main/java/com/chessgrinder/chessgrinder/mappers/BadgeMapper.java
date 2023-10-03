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
                .id(badgeEntity.getId().toString())
                .title(badgeEntity.getTitle())
                .description(badgeEntity.getDescription())
                .imageUrl(badgeEntity.getPictureUrl())
                .build();
    }
    public List<BadgeDto> toDto(List<BadgeEntity> badgeEntities) {
        return badgeEntities.stream().map(this::toDto).toList();
    }
}
