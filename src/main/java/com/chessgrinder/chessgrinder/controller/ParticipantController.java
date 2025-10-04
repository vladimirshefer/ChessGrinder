package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.ListDto;
import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoleEntity.Roles;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.mappers.ParticipantMapper;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.security.AuthenticatedUserArgumentResolver.AuthenticatedUser;
import com.chessgrinder.chessgrinder.security.util.SecurityUtil;
import com.chessgrinder.chessgrinder.service.ParticipantService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final TournamentRepository tournamentRepository;

    @GetMapping
    public ListDto<ParticipantDto> getParticipants(
            @PathVariable UUID tournamentId
    ) {
        return ListDto.of(participantMapper.toDto(participantRepository.findByTournamentId(tournamentId)));
    }

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @PostMapping
    public void addParticipantToTournament(
            @PathVariable UUID tournamentId,
            @RequestBody ParticipantDto participantDto
    ) {
        participantService.addParticipantToTheTournament(tournamentId, participantDto);
    }

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
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

    @GetMapping("/winner")
    public ListDto<ParticipantDto> getWinner(
            @PathVariable UUID tournamentId
    ){
        return ListDto.of(participantService.getWinner(tournamentId));
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
            return null;
        }
        return participantMapper.toDto(participantEntity);
    }

    /**
     * @param participantDto partial dto. fields that do not require changes are null.
     */
    @PatchMapping("/{participantId}")
    public void update(
            @SuppressWarnings("unused") // Used in PreAuthorize
            @PathVariable UUID tournamentId,
            @PathVariable UUID participantId,
            @AuthenticatedUser UserEntity user,
            @RequestBody ParticipantDto participantDto
    ) {
        participantService.update(
                tournamentId,
                participantId,
                user,
                participantDto
        );
    }

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @PostMapping("/{participantId}/action/miss")
    public void miss(
            @SuppressWarnings("unused") // Used in PreAuthorize
            @PathVariable UUID tournamentId,
            @PathVariable UUID participantId
    ) {
        ParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No participant with id " + participantId));
        participant.setMissing(true);
        participantRepository.save(participant);
    }

    @PostMapping("/me/action/miss")
    public void miss(
            @AuthenticatedUser UserEntity user,
            @SuppressWarnings("unused") // Used in PreAuthorize
            @PathVariable UUID tournamentId
    ) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId).orElseThrow();
        ParticipantEntity participant = participantRepository.findByTournamentIdAndUserId(tournamentId, user.getId());

        switch (tournament.getStatus()) {
            case FINISHED -> {
                throw new ResponseStatusException(400, "Tournament already finished. Ask administrator for help.", null);
            }
            case ACTIVE -> {
                participant.setMissing(true);
                participantRepository.save(participant);
            }
            case PLANNED -> {
                participantRepository.delete(participant);
            }
        }

    }

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @PostMapping("/{participantId}/action/unmiss")
    public void unmiss(
            @SuppressWarnings("unused") // Used in PreAuthorize
            @PathVariable UUID tournamentId,
            @PathVariable UUID participantId
    ) {
        ParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No participant with id " + participantId));
        participant.setMissing(false);
        participantRepository.save(participant);
    }

}
