package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.repositories.ClubRepository;
import com.chessgrinder.chessgrinder.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubRepository clubRepository;

    private final ClubService clubService;

    private static final String DEFAULT_NAME = "DEFAULT CLUB";
    private static final String DEFAULT_DESCRIPTION = "DEFAULT DESCRIPTION";
    private static final String DEFAULT_LOCATION = "DEFAULT LOCATION";

    @GetMapping
    public ListDto<ClubDto> getAllClubs() {
        List<ClubDto> allClubs = clubService.getAllClubs();
        return ListDto.<ClubDto>builder().values(allClubs).build();
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping("/createDefaultClub")
    public void createClub() {
        final var clubDto = ClubDto.builder()
                .name(DEFAULT_NAME)
                .description(DEFAULT_DESCRIPTION)
                .location(DEFAULT_LOCATION)
                .build();

        createClub(clubDto);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping("/createCustomClub")
    public void createClub(
            @RequestBody ClubDto clubDto
    ) {
        ClubEntity club = ClubEntity.builder()
                .id(UUID.randomUUID())
                .name(clubDto.getName())
                .description(clubDto.getDescription())
                .location(clubDto.getLocation())
                .build();

        clubRepository.save(club);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @PatchMapping
    public void updateClub(
            @RequestBody ClubDto clubDto
    ) {
        final var clubId = UUID.fromString(clubDto.getId());
        ClubEntity club = clubRepository.findById(clubId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No club with id " + clubId)
        );

        if (clubDto.getName() != null) {
            club.setName(clubDto.getName());
        }
        if (clubDto.getDescription() != null) {
            club.setDescription(clubDto.getDescription());
        }
        if (clubDto.getLocation() != null) {
            club.setLocation(clubDto.getLocation());
        }

        clubRepository.save(club);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @DeleteMapping("/{clubId}")
    public void deleteClub(
            @PathVariable UUID clubId
    ) {
        if (!clubRepository.existsById(clubId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No club with id " + clubId);
        }

        clubRepository.deleteById(clubId);
    }
}
