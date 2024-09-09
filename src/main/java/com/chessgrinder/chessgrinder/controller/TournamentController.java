package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.SubmitMatchResultRequestDto;
import com.chessgrinder.chessgrinder.dto.TournamentDto;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.mappers.TournamentMapper;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.AuthenticatedUserArgumentResolver.AuthenticatedUser;
import com.chessgrinder.chessgrinder.service.TournamentService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tournament")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;
    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final TournamentMapper tournamentMapper;

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping
    public TournamentDto createTournament() {
        return tournamentService.createTournament(LocalDateTime.now());
    }

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @GetMapping("/{tournamentId}/action/start")
    public void startTournament(
            @PathVariable UUID tournamentId
    ) {
        tournamentService.startTournament(tournamentId);
    }

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @GetMapping("/{tournamentId}/action/finish")
    public void finishTournament(@PathVariable UUID tournamentId) {
        tournamentService.finishTournament(tournamentId);
    }

    @GetMapping
    public Object getTournaments() {
        return Map.of("tournaments", tournamentService.findTournaments());
    }

    @GetMapping("/{tournamentId}")
    public TournamentDto getTournament(
            @PathVariable UUID tournamentId
    ) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(404, "No tournament with id " + tournamentId, null));
        return tournamentMapper.toDto(tournamentEntity);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @PutMapping("/{tournamentId}")
    public void updateTournament(
            @PathVariable UUID tournamentId,
            @RequestBody TournamentDto tournamentDto
    ) {
        tournamentService.updateTournament(tournamentId, tournamentDto);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @DeleteMapping("/{tournamentId}")
    public void deleteTournament(@PathVariable UUID tournamentId) {
        tournamentService.deleteTournament(tournamentId);
    }

    @PostMapping("{tournamentId}/action/participate")
    public Object participate(
            @PathVariable
            UUID tournamentId,
            @RequestParam(required = false)
            @Nullable
            String nickname,
            Authentication authentication
    ) {
        if (!authentication.isAuthenticated()) {
            throw new ResponseStatusException(401, "", null);
        }

        UserEntity user = userRepository.findByUsername(authentication.getName());
        TournamentEntity tournament = tournamentRepository.findById(tournamentId).orElseThrow();

        if (!tournament.getStatus().equals(TournamentStatus.PLANNED)) {
            throw new ResponseStatusException(400, "Tournament already started. Ask administrator for help.", null);
        }

        ParticipantEntity participant = ParticipantEntity.builder()
                .tournament(tournament)
                .user(user)
                .nickname(nickname)
                .score(BigDecimal.ZERO)
                .buchholz(BigDecimal.ZERO)
                .place(0)
                .build();

        participantRepository.save(participant);

        return authentication.getName();
    }

    @PostMapping("{tournamentId}/action/submitMyResult")
    public void submitMyResult(
            @PathVariable
            UUID tournamentId,
            @AuthenticatedUser
            UserEntity authenticatedUser,
            @RequestBody
            SubmitMatchResultRequestDto resultRequestDto
    ) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(404, "No tournament with id " + tournamentId, null));
        tournamentService.submitMyResult(tournament, authenticatedUser, resultRequestDto.getMatchResult());
    }
}
