package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.EloUpdateResultDto;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EloServiceImpl implements EloService {

    private final UserEloInitializerService userEloInitializerService;
    private final EloCalculationStrategy eloCalculationStrategy;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;


    @Value("${chessgrinder.feature.eloServiceEnabled:true}")
    private boolean eloServiceEnabled;

    @Override
    @Transactional
    public void processTournamentAndUpdateElo(TournamentEntity tournament) {

        if (!eloServiceEnabled) {
            System.out.println("Elo Service is disabled, skipping processing.");
            return;
        }

        Map<UUID, Integer> currentEloMap = initializeAuthorizedParticipantsElo(tournament);

        processMatches(tournament, currentEloMap);

        finalizeTournament(tournament);
    }

    private Map<UUID, Integer> initializeAuthorizedParticipantsElo(TournamentEntity tournament) {
        Map<UUID, Integer> currentEloMap = new HashMap<>();

        for (RoundEntity round : tournament.getRounds()) {
            for (MatchEntity match : round.getMatches()) {
                initializeParticipantElo(match.getParticipant1(), currentEloMap);
                initializeParticipantElo(match.getParticipant2(), currentEloMap);
            }
        }
        return currentEloMap;
    }

    private void initializeParticipantElo(ParticipantEntity participant, Map<UUID, Integer> currentEloMap) {
        if (participant == null || participant.getUser() == null) {
            return;
        }

        UserEntity user = participant.getUser();

        userEloInitializerService.setDefaultEloIfNeeded(user, SecurityUtil.isAuthorizedUser(user));

        currentEloMap.putIfAbsent(participant.getId(), user.getEloPoints());
    }

    private void processMatches(TournamentEntity tournament, Map<UUID, Integer> currentEloMap) {
        for (RoundEntity round : tournament.getRounds()) {
            for (MatchEntity match : round.getMatches()) {
                processSingleMatch(match, currentEloMap);
            }
        }
    }

    private void processSingleMatch(MatchEntity match, Map<UUID, Integer> currentEloMap) {
        ParticipantEntity participant1 = match.getParticipant1();
        ParticipantEntity participant2 = match.getParticipant2();

        if (participant1 == null || participant2 == null) {
            return;
        }

        UserEntity user1 = participant1.getUser();
        UserEntity user2 = participant2.getUser();

        boolean isUser1Authorized = SecurityUtil.isAuthorizedUser(user1);
        boolean isUser2Authorized = SecurityUtil.isAuthorizedUser(user2);

        if (!isUser1Authorized && !isUser2Authorized) {
            return;
        }

        setInitialElo(participant1, user1, isUser1Authorized, currentEloMap);
        setInitialElo(participant2, user2, isUser2Authorized, currentEloMap);

        calculateAndApplyElo(match, participant1, participant2, currentEloMap);
    }

    private void setInitialElo(ParticipantEntity participant, UserEntity user, boolean isAuthorized, Map<UUID, Integer> currentEloMap) {
        if (isAuthorized && participant.getInitialEloPoints() == 0) {
            participant.setInitialEloPoints(user.getEloPoints());
            currentEloMap.put(participant.getId(), user.getEloPoints());
            participantRepository.save(participant);
        }
    }

    private void calculateAndApplyElo(MatchEntity match, ParticipantEntity participant1, ParticipantEntity participant2, Map<UUID, Integer> currentEloMap) {
        int whiteElo = currentEloMap.getOrDefault(participant1.getId(), 0);
        int blackElo = currentEloMap.getOrDefault(participant2.getId(), 0);

        boolean bothUsersAuthorized = SecurityUtil.isAuthorizedUser(participant1.getUser()) && SecurityUtil.isAuthorizedUser(participant2.getUser());

        EloUpdateResultDto updateResult = eloCalculationStrategy.calculateElo(whiteElo, blackElo, match.getResult(), bothUsersAuthorized);

        currentEloMap.put(participant1.getId(), updateResult.getWhiteNewElo());
        currentEloMap.put(participant2.getId(), updateResult.getBlackNewElo());

        updateUserElo(participant1.getUser(), updateResult.getWhiteNewElo());
        updateUserElo(participant2.getUser(), updateResult.getBlackNewElo());
    }

    private void updateUserElo(UserEntity user, int newElo) {
        if (SecurityUtil.isAuthorizedUser(user)) {
            user.setEloPoints(newElo);
            userRepository.save(user);
        }
    }

    private void finalizeTournament(TournamentEntity tournament) {
        tournament.setHasEloCalculated(true);
        tournamentRepository.save(tournament);
    }

    @Override
    public void rollbackEloChanges(TournamentEntity tournament) {

        if (!eloServiceEnabled) {
            System.out.println("Elo Service is disabled, rollback skipped.");
            return;
        }

        List<MatchEntity> matches = tournament.getRounds().stream()
                .flatMap(round -> round.getMatches().stream())
                .toList();

        for (MatchEntity match : matches) {
            ParticipantEntity participant1 = match.getParticipant1();
            ParticipantEntity participant2 = match.getParticipant2();

            if (participant1 != null) {
                UserEntity user1 = participant1.getUser();
                if (user1 != null && participant1.getInitialEloPoints() > 0) {

                    int earnedElo1 = user1.getEloPoints() - participant1.getInitialEloPoints();
                    int newElo1 = user1.getEloPoints() - earnedElo1;

                    user1.setEloPoints(newElo1);
                    userRepository.save(user1);
                }
            }

            if (participant2 != null) {
                UserEntity user2 = participant2.getUser();
                if (user2 != null && participant2.getInitialEloPoints() > 0) {
                    // Вычитаем заработанное за турнир количество очков
                    int earnedElo2 = user2.getEloPoints() - participant2.getInitialEloPoints();
                    int newElo2 = user2.getEloPoints() - earnedElo2;

                    user2.setEloPoints(newElo2);
                    userRepository.save(user2);
                }
            }
        }

        tournament.setHasEloCalculated(false);
        tournamentRepository.save(tournament);
    }
}
