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
    private final ParticipantRepository participantRepository;
    private final SwissService swissService;

    public void createRound(UUID tournamentId) {

        Round lastExistedRound = roundRepository.findFirstByTournamentIdOrderByNumberDesc(tournamentId);
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

    public void finishRound(UUID tournamentId, Integer roundNumber) {
        Round round = roundRepository.findByTournamentIdAndNumber(tournamentId, roundNumber);
        if (round != null) {
            round.setFinished(true);
            roundRepository.save(round);
        }
    }

    public void reopenRound(UUID tournamentId, Integer roundNumber) {
        Round round = roundRepository.findByTournamentIdAndNumber(tournamentId, roundNumber);
        if (round != null) {
            round.setFinished(false);
            roundRepository.save(round);
        }
    }

    public void makeMatchUp(UUID tournamentId, Integer roundNumber) {

        List<Participant> participants = participantRepository.findByTournamentId(tournamentId);
        Round activeRoundInTournament = roundRepository.findByTournamentIdAndNumber(tournamentId, roundNumber);

        List<Match> matchesInTheRound = swissService.makePairs(participants);
        activeRoundInTournament.setMatches(matchesInTheRound);

        roundRepository.save(activeRoundInTournament);
    }

    public void markUserAsMissedInTournament(UUID userId, UUID tournamentId) {

        Participant participant = participantRepository.findByTournamentIdAndUserId(tournamentId, userId);
        participant.setMissing(true);
        participantRepository.save(participant);


    }
}
