package com.chessgrinder.chessgrinder.service;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;

    public void submitMatchResult(UUID matchId, SubmitMatchResultRequestDto submitMatchResultDto) {

        matchRepository.findById(matchId).ifPresent(match -> {
            match.setResult(submitMatchResultDto.getMatchResult());
            matchRepository.save(match);
        });
    }
}
