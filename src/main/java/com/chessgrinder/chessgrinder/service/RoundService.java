package com.chessgrinder.chessgrinder.service;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class RoundService {

    private final RoundRepository roundRepository;

    public void createRound(UUID tournamentId) {

        Round lastExistedRound = roundRepository.findFirstByTournamentIdOrderByNumberDesc(tournamentId);
        lastExistedRound.setFinished(true);
        Integer nextRoundNumber = lastExistedRound.getNumber() + 1;

        Round nextRound = Round.builder()
                .id(UUID.randomUUID())
                .number(nextRoundNumber)
                .tournament(lastExistedRound.getTournament())
                .matches(List.of())
                .isFinished(false)
                .build();

        //TODO do not create round if the tournament is finished

        roundRepository.save(lastExistedRound);
        roundRepository.save(nextRound);
    }
}
