package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.chessengine.ratings.EloCalculationStrategy;
import com.chessgrinder.chessgrinder.chessengine.ratings.EloHolder;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.service.TournamentService.TournamentListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
@Slf4j
public class RatingsTournamentListenerImpl implements TournamentListener {

    private final EloCalculationStrategy eloCalculationStrategy;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;

    @Value("${chessgrinder.feature.chess.rating:false}")
    private boolean eloServiceEnabled;

    @Override
    @Transactional
    public void tournamentFinished(TournamentEntity tournament) {
        if (!eloServiceEnabled) {
            log.info("Elo Service is disabled, skipping processing.");
            return;
        }

        EloHolder eloHolder = new EloHolder();

        for (RoundEntity round1 : tournament.getRounds()) {
            for (MatchEntity match1 : round1.getMatches()) {
                eloHolder.putInitial(match1.getParticipant1());
                eloHolder.putInitial(match1.getParticipant2());
            }
        }

        for (RoundEntity round : tournament.getRounds()) {
            for (MatchEntity match : round.getMatches()) {
                processSingleMatch(match, eloHolder);
            }
        }

        for (UUID participantId : eloHolder.getResult().keySet()) {
            ParticipantEntity participant = participantRepository.findById(participantId).orElse(null);
            if (participant == null) {
                continue;
            }
            saveParticipantInitialElo(participant, eloHolder.getInitial(participantId));
            saveParticipantFinalElo(participant, eloHolder.getDiff(participantId));
            UserEntity user = participant.getUser();
            Integer result = eloHolder.getResult(participantId);
            if (user != null && result != null) {
                saveUserElo(user, result);
            }
        }

        tournament.setHasEloCalculated(true);
        tournamentRepository.save(tournament);
    }

    private void processSingleMatch(MatchEntity match, EloHolder eloHolder) {
        ParticipantEntity participant1 = match.getParticipant1();
        ParticipantEntity participant2 = match.getParticipant2();

        if (participant1 == null || participant2 == null) {
            return;
        }

        UserEntity user1 = participant1.getUser();
        UserEntity user2 = participant2.getUser();

        if (user1 == null && user2 == null) {
            return;
        }

        int whiteElo = eloHolder.getResultOr(participant1.getId(), eloHolder.getInitial(participant1.getId()));
        int blackElo = eloHolder.getResultOr(participant2.getId(), eloHolder.getInitial(participant2.getId()));

        boolean bothUsersAuthorized = participant1.getUser() != null && participant2.getUser() != null;
        var updateResult = eloCalculationStrategy.calculateElo(whiteElo, blackElo, match.getResult(), bothUsersAuthorized);

        eloHolder.putResult(participant1.getId(), updateResult.whiteElo());
        eloHolder.putResult(participant2.getId(), updateResult.blackElo());
    }

    private void saveParticipantInitialElo(ParticipantEntity participant, int elo) {
        participant.setInitialEloPoints(elo);
        participantRepository.save(participant);
    }

    private void saveParticipantFinalElo(ParticipantEntity participant, int elo) {
        participant.setFinalEloPoints(elo);
        participantRepository.save(participant);
    }

    private void saveUserElo(UserEntity user, int newElo) {
        user.setEloPoints(newElo);
        userRepository.save(user);
    }

    @Override
    public void tournamentReopened(TournamentEntity tournament) {
        if (!tournament.isHasEloCalculated()) {
            return;
        }

        Set<ParticipantEntity> uniqueParticipants = tournament.getRounds().stream()
                .flatMap(round -> round.getMatches().stream())
                .flatMap(match -> Stream.of(match.getParticipant1(), match.getParticipant2()))
                .filter(participant -> participant != null && participant.getUser() != null)
                .collect(Collectors.toSet());

        for (ParticipantEntity participant : uniqueParticipants) {
            UserEntity user = participant.getUser();

            if (participant.getFinalEloPoints() != 0 && user != null) {
                int originalElo = Math.max(user.getEloPoints() - participant.getFinalEloPoints(), 0);
                saveUserElo(user, originalElo);
            }

            saveParticipantFinalElo(participant, 0);

        }

        tournament.setHasEloCalculated(false);
        tournamentRepository.save(tournament);
    }

    @Override
    @Transactional
    public void totalReset() {
        userRepository.clearAllEloPoints();
        participantRepository.clearAllEloPoints();
        tournamentRepository.clearAllEloPoints();
    }
}
