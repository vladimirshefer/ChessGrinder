package com.chessgrinder.chessgrinder.controller;

import java.time.*;
import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.service.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournament")
@CrossOrigin(origins = "localhost:3000")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping()
    public void createTournament(@RequestBody CreateTournamentRequestDto request) {
        tournamentService.createTournament(request.getDate());
    }

}
