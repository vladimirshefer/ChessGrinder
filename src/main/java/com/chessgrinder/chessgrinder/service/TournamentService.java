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

    private final SwissService swissService;
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;
    private final ParticipantRepository participantRepository;

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

    public void startTournament(UUID tournamentId) {

        List<Participant> participants = participantRepository.findByTournamentId(tournamentId);
        Round activeRoundInTournament = roundRepository.findActiveRoundInTournament(tournamentId);

        List<Match> matchesInTheRound = swissService.makePairs(participants);

        activeRoundInTournament.setMatches(matchesInTheRound);

        tournamentRepository.findById(tournamentId).ifPresent(tournament -> {
            tournament.setStatus(TournamentStatus.ACTIVE);
            tournamentRepository.save(tournament);
        });

        roundRepository.save(activeRoundInTournament);
    }

    public void finishTournament(UUID tournamentId) {
        tournamentRepository.findById(tournamentId).ifPresent(tournament -> {
            tournament.setStatus(TournamentStatus.FINISHED);
            tournamentRepository.save(tournament);
        });
    }



}
