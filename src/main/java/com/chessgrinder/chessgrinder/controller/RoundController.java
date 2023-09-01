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
}
