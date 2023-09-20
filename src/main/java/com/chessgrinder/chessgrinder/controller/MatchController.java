package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.SubmitMatchResultRequestDto;
import com.chessgrinder.chessgrinder.entities.Role;
import com.chessgrinder.chessgrinder.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tournament/{tournamentId}/round/{roundId}/match/{matchId}")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @Secured(Role.Roles.ADMIN)
    @PostMapping()
    public void submitMatchResult(
            @PathVariable UUID matchId,
            @RequestBody SubmitMatchResultRequestDto submitMatchResultDto
    ) {
        matchService.submitMatchResult(matchId, submitMatchResultDto);
    }
}
