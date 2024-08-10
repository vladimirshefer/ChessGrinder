package com.chessgrinder.chessgrinder.service;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.repositories.*;
import com.chessgrinder.chessgrinder.security.entitypermissionevaluator.EntityPermissionEvaluator;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final EntityPermissionEvaluator<TournamentEntity> tournamentPermissionEvaluator;

    @Transactional
    public void submitMatchResult(UserEntity user, UUID matchId, SubmitMatchResultRequestDto submitMatchResultDto) {
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No match with id " + matchId));
        MatchResult result = submitMatchResultDto.getMatchResult();
        if (tournamentPermissionEvaluator.hasPermission(user, match.getRound().getTournament().getId().toString(), "MODERATOR")) {
            match.setResult(result);
            matchRepository.save(match);
            return;
        }
        ParticipantEntity participant1 = match.getParticipant1();
        if (participant1 != null) {
            UserEntity user1 = participant1.getUser();
            if (user1 != null && user1.getId().equals(user.getId())) {
                match.setResultSubmittedByParticipant1(result);
            }
        }
        ParticipantEntity participant2 = match.getParticipant2();
        if (participant2 != null) {
            UserEntity user2 = participant2.getUser();
            if (user2 != null && user2.getId().equals(user.getId())) {
                match.setResultSubmittedByParticipant2(result);
            }
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to change result");
    }
}
