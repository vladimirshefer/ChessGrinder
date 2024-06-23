package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.mappers.ParticipantMapper;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.security.AuthenticatedUserArgumentResolver.AuthenticatedUser;
import com.chessgrinder.chessgrinder.service.ParticipantService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/tournament/{tournamentId}/participant")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping
    public void addParticipantToTournament(
            @PathVariable UUID tournamentId,
            @RequestBody ParticipantDto participantDto
    ) {
        participantService.addParticipantToTheTournament(tournamentId, participantDto);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @DeleteMapping("/{participantId}")
    public void delete(
            @PathVariable UUID tournamentId,
            @PathVariable UUID participantId
    ) {
        participantService.delete(participantId);
    }

    @GetMapping("/{participantId}")
    public ParticipantDto getParticipant(
            @PathVariable UUID tournamentId,
            @PathVariable UUID participantId
    ) {
        return participantService.get(participantId);
    }

    @GetMapping("/me")
    public ParticipantDto getMe(
            @PathVariable UUID tournamentId,
            @AuthenticatedUser(required = false)
            @Nullable
            UserEntity authenticatedUser
    ) {
        if (authenticatedUser == null) {
            return null;
        }

        ParticipantEntity participantEntity = participantRepository.findByTournamentIdAndUserId(tournamentId, authenticatedUser.getId());
        if (participantEntity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not participating");
        }
        return participantMapper.toDto(participantEntity);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @PutMapping("/{participantId}")
    public void update(
            @PathVariable UUID participantId,
            @RequestBody ParticipantDto participantDto
    ) {
        ParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No participant with id " + participantId));
        participant.setNickname(participantDto.getName());
        participantRepository.save(participant);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping("/{participantId}/action/miss")
    public void miss(
            @PathVariable UUID participantId
    ) {
        ParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No participant with id " + participantId));
        participant.setMissing(true);
        participantRepository.save(participant);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping("/{participantId}/action/unmiss")
    public void unmiss(
            @PathVariable UUID participantId
    ) {
        ParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No participant with id " + participantId));
        participant.setMissing(false);
        participantRepository.save(participant);
    }
}
