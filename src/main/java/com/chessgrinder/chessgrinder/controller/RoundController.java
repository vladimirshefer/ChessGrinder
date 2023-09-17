package com.chessgrinder.chessgrinder.controller;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.pages.*;
import com.chessgrinder.chessgrinder.service.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournament/{tournamentId}/round")
@CrossOrigin(origins = "localhost:3000")
@RequiredArgsConstructor
public class RoundController {

    private final RoundService roundService;

    @PostMapping()
    public void createNextRound(@PathVariable UUID tournamentId) {
        roundService.createRound(tournamentId);
    }

    @PostMapping("/{roundNumber}/action/finish")
    public void finishRound(@PathVariable UUID tournamentId, @PathVariable Integer roundNumber) {
        roundService.finishRound(tournamentId, roundNumber);
    }

    @PostMapping("/{roundNumber}/action/reopen")
    public void reopenRound(@PathVariable UUID tournamentId, @PathVariable Integer roundNumber) {
        roundService.reopenRound(tournamentId, roundNumber);
    }

    @PostMapping("/{roundNumber}/action/matchup")
    public void makeMatchUp(@PathVariable UUID tournamentId, @PathVariable Integer roundNumber) {
        roundService.makeMatchUp(tournamentId, roundNumber);
    }

    @PostMapping("/{roundNumber}/action/miss")
    public void markUserAsMissedInTournament(@PathVariable UUID tournamentId,
                            @RequestParam UUID userId) {
        roundService.markUserAsMissedInTournament(userId, tournamentId);
    }




}
