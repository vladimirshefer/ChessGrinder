package com.chessgrinder.chessgrinder.controller;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.service.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournament/{tournamentId}")
@CrossOrigin(origins = "localhost:3000")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantServiceService;

    @PostMapping("/participant")
    public void addParticipantToTournament(@PathVariable UUID tournamentId,
                                           @RequestBody ParticipantDto participantDto) {
        participantServiceService.addParticipantToTheTournament(tournamentId, participantDto);
    }
}
