package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.entities.Role;
import com.chessgrinder.chessgrinder.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tournament")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @Secured(Role.Roles.ADMIN)
    @PostMapping
    public void createTournament() {
        tournamentService.createTournament(LocalDateTime.now());
    }

    @Secured(Role.Roles.ADMIN)
    @GetMapping("/{tournamentId}/action/start")
    public void startTournament(@PathVariable UUID tournamentId) {
        tournamentService.startTournament(tournamentId);
    }

    @Secured(Role.Roles.ADMIN)
    @GetMapping("/{tournamentId}/action/finish")
    public void finishTournament(@PathVariable UUID tournamentId) {
        tournamentService.finishTournament(tournamentId);
    }

    @GetMapping
    public Object getTournaments() {
        return Map.of("tournaments", tournamentService.findTournaments());
    }
}
