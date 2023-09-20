package com.chessgrinder.chessgrinder.controller;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.Role;
import com.chessgrinder.chessgrinder.service.*;
import lombok.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournament/{tournamentId}")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantServiceService;

    @Secured(Role.Roles.ADMIN)
    @PostMapping("/participant")
    public void addParticipantToTournament(@PathVariable UUID tournamentId,
                                           @RequestBody ParticipantDto participantDto) {
        participantServiceService.addParticipantToTheTournament(tournamentId, participantDto);
    }
}
