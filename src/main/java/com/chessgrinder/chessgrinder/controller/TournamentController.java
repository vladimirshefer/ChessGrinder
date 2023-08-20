package com.chessgrinder.chessgrinder.controller;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.pages.*;
import com.chessgrinder.chessgrinder.service.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/page/tournament")
@RequiredArgsConstructor
public class TournamentController {
    private final TournamentPageService tournamentPageService;

    @GetMapping("{tournamentId}")
    public TournamentPageDto getDto(@PathVariable UUID tournamentId) {
        return tournamentPageService.getTournamentData(tournamentId);
    }
}
