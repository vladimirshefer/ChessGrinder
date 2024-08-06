package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.SubmitMatchResultRequestDto;
import com.chessgrinder.chessgrinder.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tournament/{tournamentId}/round/{roundId}/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @PostMapping("{matchId}")
    public void submitMatchResult(
            @PathVariable UUID tournamentId,
            @PathVariable UUID matchId,
            @RequestBody SubmitMatchResultRequestDto submitMatchResultDto
    ) {
        matchService.submitMatchResult(matchId, submitMatchResultDto);
    }

}
