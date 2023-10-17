package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.TournamentDto;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tournament")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;
    private final TournamentRepository tournamentRepository;

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping
    public void createTournament() {
        tournamentService.createTournament(LocalDateTime.now());
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @GetMapping("/{tournamentId}/action/start")
    public void startTournament(@PathVariable UUID tournamentId) {
        tournamentService.startTournament(tournamentId);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @GetMapping("/{tournamentId}/action/finish")
    public void finishTournament(@PathVariable UUID tournamentId) {
        tournamentService.finishTournament(tournamentId);
    }

    @GetMapping
    public Object getTournaments() {
        return Map.of("tournaments", tournamentService.findTournaments());
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @PutMapping("/{tournamentId}")
    public void updateTournament(
            @PathVariable UUID tournamentId,
            @RequestBody TournamentDto tournamentDto
    ) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(404, "No tournament with id " + tournamentId, null));
        tournament.setName(tournamentDto.getName());
        tournament.setDate(tournamentDto.getDate());
        tournament.setLocationName(tournamentDto.getLocationName());
        tournament.setLocationUrl(tournament.getLocationUrl());
        tournamentRepository.save(tournament);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @DeleteMapping("/{tournamentId}")
    public void deleteTournament(@PathVariable UUID tournamentId) {
        tournamentService.deleteTournament(tournamentId);
    }

    @PostMapping("{tournamentId}/action/participate")
    public Object participate(
            @PathVariable UUID tournamentId,
            Principal principal
    ) {
        return principal.getName();
    }
}
