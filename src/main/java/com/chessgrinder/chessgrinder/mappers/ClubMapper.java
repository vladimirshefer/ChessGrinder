package com.chessgrinder.chessgrinder.mappers;

import com.chessgrinder.chessgrinder.dto.ClubDto;
import com.chessgrinder.chessgrinder.entities.ClubEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClubMapper {

    public ClubDto toDto(ClubEntity club) {

        return ClubDto.builder()
                .id(club.getId().toString())
                .name(club.getName())
                .description(club.getDescription())
                .location(club.getLocation())
                .registrationDate(club.getCreatedAt())
                .build();
    }
}
