package com.chessgrinder.chessgrinder.service;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.exceptions.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoundService {
    private final TournamentRepository tournamentRepository;

    private final RoundRepository roundRepository;
    private final ParticipantRepository participantRepository;
    private final SwissService swissService;
    private final MatchRepository matchRepository;

    public void createRound(UUID tournamentId) {

        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow();
        Round lastExistedRound = roundRepository.findFirstByTournamentIdOrderByNumberDesc(tournamentId);
        Integer nextRoundNumber = lastExistedRound != null ? lastExistedRound.getNumber() + 1 : 1;

        Round nextRound = Round.builder()
                .id(UUID.randomUUID())
                .number(nextRoundNumber)
                .tournament(tournament)
                .matches(List.of())
                .isFinished(false)
                .build();

        //TODO do not create round if the tournament is finished

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
        Round round = roundRepository.findByTournamentIdAndNumber(tournamentId, roundNumber);

        List<Match> matches = swissService.makePairs(participants);
        matches.forEach(match -> match.setRound(round));
        matchRepository.saveAll(matches);
    }

    public void markUserAsMissedInTournament(UUID userId, UUID tournamentId) {
        Participant participant = participantRepository.findByTournamentIdAndUserId(tournamentId, userId);
        participant.setMissing(true);
        participantRepository.save(participant);
    }

    public void deleteRound(UUID tournamentId, Integer roundNumber) throws RoundNotFoundException {

        Round round = roundRepository.findByTournamentIdAndNumber(tournamentId, roundNumber);

        if (round != null) {
            roundRepository.delete(round);
        } else {
            log.error("There is no round with number: " + roundNumber + " in the tournament with id: " + tournamentId);
            throw new RoundNotFoundException();
        }
    }
}
