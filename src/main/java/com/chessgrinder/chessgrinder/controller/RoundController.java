package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.exceptions.RoundNotFoundException;
import com.chessgrinder.chessgrinder.service.RoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tournament/{tournamentId}/round")
@RequiredArgsConstructor
public class RoundController {

    private final RoundService roundService;

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping()
    public void createNextRound(@PathVariable UUID tournamentId) {
        roundService.createRound(tournamentId);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping("/{roundNumber}/action/finish")
    public void finishRound(@PathVariable UUID tournamentId, @PathVariable Integer roundNumber) {
        roundService.finishRound(tournamentId, roundNumber);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping("/{roundNumber}/action/reopen")
    public void reopenRound(@PathVariable UUID tournamentId, @PathVariable Integer roundNumber) {
        roundService.reopenRound(tournamentId, roundNumber);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping("/{roundNumber}/action/matchup")
    public void makeMatchUp(@PathVariable UUID tournamentId, @PathVariable Integer roundNumber) {
        roundService.makeMatchUp(tournamentId, roundNumber);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping("/{roundNumber}/action/miss")
    public void markUserAsMissedInTournament(@PathVariable UUID tournamentId,
                            @RequestParam UUID userId) {
        roundService.markUserAsMissedInTournament(userId, tournamentId);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @DeleteMapping("/{roundNumber}")
    public void deleteRound(@PathVariable UUID tournamentId,
                            @RequestParam Integer roundNumber) throws RoundNotFoundException {
        roundService.deleteRound(tournamentId, roundNumber);
    }

}
