package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/tournament")
@CrossOrigin(origins = "localhost:3000")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping
    public void createTournament() {
        tournamentService.createTournament(LocalDateTime.now());
    }

    @GetMapping("/{tournamentId}/start")
    public void startTournament(@PathVariable UUID tournamentId) {
        tournamentService.startTournament(tournamentId);
    }

    @GetMapping("/{tournamentId}/finish")
    public void finishTournament(@PathVariable UUID tournamentId) {
        tournamentService.finishTournament(tournamentId);
    }
}
