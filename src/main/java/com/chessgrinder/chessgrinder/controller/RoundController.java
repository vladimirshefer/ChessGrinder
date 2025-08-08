package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.service.RoundService;
import com.chessgrinder.chessgrinder.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tournament/{tournamentId}/round")
@RequiredArgsConstructor
public class RoundController {

    private final RoundService roundService;
    private final TournamentService tournamentService;

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @PostMapping()
    public void createNextRound(@PathVariable UUID tournamentId) {
        roundService.createRound(tournamentId);
    }

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @PostMapping("/{roundNumber}/action/finish")
    public void finishRound(@PathVariable UUID tournamentId, @PathVariable Integer roundNumber) {
        roundService.finishRound(tournamentId, roundNumber);
        if (tournamentService.isReadyToFinish(tournamentId)) {
            tournamentService.finishTournament(tournamentId);
        }
    }

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @PostMapping("/{roundNumber}/action/reopen")
    public void reopenRound(@PathVariable UUID tournamentId, @PathVariable Integer roundNumber) {
        roundService.reopenRound(tournamentId, roundNumber);
    }

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @PostMapping("/{roundNumber}/action/matchup")
    public void makePairings(@PathVariable UUID tournamentId, @PathVariable Integer roundNumber) {
        roundService.makePairings(tournamentId, roundNumber);
    }

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @PostMapping("/{roundNumber}/action/miss")
    public void markUserAsMissedInTournament(@PathVariable UUID tournamentId,
                            @RequestParam UUID userId) {
        roundService.markUserAsMissedInTournament(userId, tournamentId);
    }

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @DeleteMapping("/{roundNumber}")
    public void deleteRound(@PathVariable UUID tournamentId,
                            @PathVariable Integer roundNumber) {
        roundService.deleteRound(tournamentId, roundNumber);
    }
}
