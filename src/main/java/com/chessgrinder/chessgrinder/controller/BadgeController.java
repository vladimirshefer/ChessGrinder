package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.BadgeDto;
import com.chessgrinder.chessgrinder.dto.ListDto;
import com.chessgrinder.chessgrinder.entities.BadgeEntity;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.mappers.BadgeMapper;
import com.chessgrinder.chessgrinder.repositories.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/badge")
@RequiredArgsConstructor
public class BadgeController {
    private final BadgeRepository badgeRepository;
    private final BadgeMapper badgeMapper;

    @GetMapping
    public ListDto<BadgeDto> getBadges() {
        return ListDto.<BadgeDto>builder().values(badgeMapper.toDto(badgeRepository.findAll())).build();
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping
    public BadgeDto create(@RequestBody BadgeDto badgeDto) {
        return badgeMapper.toDto(badgeRepository.save(BadgeEntity.builder()
                        .description(badgeDto.getDescription())
                        .title(badgeDto.getTitle())
                        .pictureUrl(badgeDto.getImageUrl())
                .build()
        ));
    }

}
