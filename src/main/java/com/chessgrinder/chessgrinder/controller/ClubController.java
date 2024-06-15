package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.mappers.ClubMapper;
import com.chessgrinder.chessgrinder.repositories.ClubRepository;
import com.chessgrinder.chessgrinder.service.ClubService;
import com.chessgrinder.chessgrinder.utils.Const;
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

    private final ClubMapper clubMapper;

    @GetMapping
    public ListDto<ClubDto> getAllClubs() {
        List<ClubDto> allClubs = clubService.getAllClubs();
        return ListDto.<ClubDto>builder().values(allClubs).build();
    }

    @GetMapping("/{clubId}")
    public ClubDto getClubById(
            @PathVariable UUID clubId
    ) {
        final var club = clubRepository.getById(clubId);
        return clubMapper.toDto(club);
    }

    @Secured(Const.Roles.ADMIN)
    @PostMapping
    public void createClub(
            @RequestBody ClubDto clubDto
    ) {
        ClubEntity club = ClubEntity.builder()
                .name(clubDto.getName())
                .description(clubDto.getDescription())
                .location(clubDto.getLocation())
                .build();

        clubRepository.save(club);
    }

    @Secured(Const.Roles.ADMIN)
    @PutMapping("/{clubId}")
    public void updateClub(
            @PathVariable UUID clubId,
            @RequestBody ClubDto clubDto
    ) {
        ClubEntity club = clubRepository.getById(clubId);

        club.setName(clubDto.getName());
        club.setDescription(clubDto.getDescription());
        club.setLocation(clubDto.getLocation());

        clubRepository.save(club);
    }

    @Secured(Const.Roles.ADMIN)
    @DeleteMapping("/{clubId}")
    public void deleteClub(
            @PathVariable UUID clubId
    ) {
        if (!clubRepository.existsById(clubId)) {
            throw new ResponseStatusException(HttpStatus.OK, "No club with id " + clubId);
        }

        clubRepository.deleteById(clubId);
    }
}
