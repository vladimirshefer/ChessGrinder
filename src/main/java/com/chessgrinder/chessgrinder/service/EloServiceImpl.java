package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.chessgrinder.chessgrinder.service.UserEloInitializerService.DEFAULT_ELO_POINTS;

@RequiredArgsConstructor
@Service
@Slf4j
public class EloServiceImpl implements EloService {

    private final EloCalculationStrategy eloCalculationStrategy;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;


    @Value("${chessgrinder.feature.chess.rating:false}")
    private boolean eloServiceEnabled;

    @Override
    @Transactional
    public void processTournamentAndUpdateElo(TournamentEntity tournament) {
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

        for (Map.Entry<UUID, Integer> entry : eloHolder.getResult().entrySet()) {
            updateParticipantElo(entry.getKey(), eloHolder);
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

        int whiteElo = eloHolder.getResultOr(participant1.getId(), DEFAULT_ELO_POINTS);
        int blackElo = eloHolder.getResultOr(participant2.getId(), DEFAULT_ELO_POINTS);

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

    private void updateParticipantElo(UUID participantId, EloHolder eloHolder) {
        ParticipantEntity participant = participantRepository.findById(participantId).orElse(null);
        if (participant == null) {
            return;
        }
        int initialElo = eloHolder.getInitial(participantId);
        saveParticipantInitialElo(participant, initialElo);
        Integer result = eloHolder.getResult(participantId);
        saveParticipantFinalElo(participant, result != null ? result - initialElo : DEFAULT_ELO_POINTS);
        UserEntity user = participant.getUser();
        if (user != null) {
            saveUserElo(user, result != null ? result : 0);
        }
    }

    private void saveUserElo(UserEntity user, int newElo) {
        user.setEloPoints(newElo);
        userRepository.save(user);
    }

    @Override
    public void rollbackEloChanges(TournamentEntity tournament) {
        if (!eloServiceEnabled) {
            log.info("Elo Service is disabled, rollback skipped.");
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

    public static class EloHolder {
        private final Map<UUID, Integer> participantId2InitialElo = new HashMap<>();
        private final Map<UUID, Integer> participantId2ResultElo = new HashMap<>();

        public void putInitial(UUID participantId, int elo) {
            participantId2InitialElo.put(participantId, elo);
        }

        public void putResult(UUID participantId, int elo) {
            participantId2ResultElo.put(participantId, elo);
        }

        public int getInitial(UUID participantId) {
            return participantId2InitialElo.getOrDefault(participantId, DEFAULT_ELO_POINTS);
        }

        @Nullable
        public Integer getResult(UUID participantId) {
            return participantId2ResultElo.get(participantId);
        }


        private Integer getResultOr(UUID participantId, int defaultEloPoints) {
            Integer orDefault = this.getResult(participantId);
            if (orDefault == null || orDefault == 0) return defaultEloPoints;
            else return orDefault;
        }

        public void putInitial(ParticipantEntity participant) {
            if (participant == null) {
                return;
            }

            UUID pid = participant.getId();
            if (participant.getInitialEloPoints() != 0) {
                putInitial(pid, participant.getInitialEloPoints());
            } else if (participant.getUser() != null && participant.getUser().getEloPoints() != 0) {
                putInitial(pid, participant.getUser().getEloPoints());
            } else {
                putInitial(pid, DEFAULT_ELO_POINTS);
            }
        }

        public Map<UUID, Integer> getInitial() {
            return participantId2InitialElo;
        }

        public Map<UUID, Integer> getResult() {
            return participantId2ResultElo;
        }
    }
}
