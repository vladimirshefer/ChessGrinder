package com.chessgrinder.chessgrinder.controller;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.pages.*;
import com.chessgrinder.chessgrinder.service.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pages/tournament")
@CrossOrigin(origins = "localhost:3000")
@RequiredArgsConstructor
public class TournamentPageController {
    private final TournamentPageService tournamentPageService;

    @GetMapping("{tournamentId}")
    public TournamentPageDto getDto(@PathVariable UUID tournamentId) {
        return tournamentPageService.getTournamentData(tournamentId);
    }
}
