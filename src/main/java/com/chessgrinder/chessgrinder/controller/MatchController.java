package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.SubmitMatchResultRequestDto;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.security.AuthenticatedUserArgumentResolver.AuthenticatedUser;
import com.chessgrinder.chessgrinder.security.entitypermissionevaluator.EntityPermissionEvaluator;
import com.chessgrinder.chessgrinder.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/tournament/{tournamentId}/round/{roundId}/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final EntityPermissionEvaluator<TournamentEntity> tournamentPermissionEvaluator;

    @PostMapping("{matchId}")
    public void submitMatchResult(
            @PathVariable UUID tournamentId,
            @PathVariable UUID matchId,
            @RequestBody SubmitMatchResultRequestDto submitMatchResultDto,
            @AuthenticatedUser UserEntity userEntity
            ) {
        if (tournamentPermissionEvaluator.hasPermission(userEntity, tournamentId.toString(), "MODERATOR")) {
            matchService.submitMatchResult(matchId, submitMatchResultDto);
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to change result");
    }

}
