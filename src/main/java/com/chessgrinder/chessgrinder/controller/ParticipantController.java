package com.chessgrinder.chessgrinder.controller;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.service.*;
import lombok.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournament/{tournamentId}/participant")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping
    public void addParticipantToTournament(@PathVariable UUID tournamentId,
                                           @RequestBody ParticipantDto participantDto) {
        participantService.addParticipantToTheTournament(tournamentId, participantDto);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @DeleteMapping("/{userId}")
    public void delete(
            @PathVariable UUID tournamentId,
            @PathVariable UUID userId
    ) {
        participantService.delete(tournamentId, userId);
    }
}
