package com.chessgrinder.chessgrinder.service;

import java.time.*;
import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.apache.commons.lang3.time.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Component
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;

    @Transactional
    public void createTournament(LocalDateTime date) {


        Tournament tournament = Tournament.builder()
                .id(UUID.randomUUID())
                .date(date)
                .status(TournamentStatus.PLANNED)
                .build();

        tournament = tournamentRepository.save(tournament);
        Round firstRound = Round.builder()
                .id(UUID.randomUUID())
                .tournament(tournament)
                .matches(List.of())
                .number(1)
                .isFinished(false)
                .build();

        roundRepository.save(firstRound);
    }
}
