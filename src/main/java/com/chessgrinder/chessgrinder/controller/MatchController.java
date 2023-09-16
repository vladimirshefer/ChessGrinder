package com.chessgrinder.chessgrinder.controller;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.service.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournament/{tournamentId}/round/{roundId}/match/{matchId}")
@CrossOrigin(origins = "localhost:3000")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping()
    public void submitMatchResult(
            @PathVariable UUID matchId,
            @RequestBody SubmitMatchResultRequestDto submitMatchResultDto
    ) {
        matchService.submitMatchResult(matchId, submitMatchResultDto);
    }
}
