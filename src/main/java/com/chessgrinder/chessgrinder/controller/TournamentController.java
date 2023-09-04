package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/tournament")
@CrossOrigin(origins = "localhost:3000")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping
    public void createTournament() {
        tournamentService.createTournament(LocalDateTime.now());
    }

}
