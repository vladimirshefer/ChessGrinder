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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class EloServiceImpl implements EloService {

    private final UserEloInitializerService userEloInitializerService;
    private final EloCalculationStrategy eloCalculationStrategy;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;


    @Value("${chessgrinder.feature.eloServiceEnabled:false}")
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

        finalizeTournament(tournament, currentEloMap);
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

        if (participant.getInitialEloPoints() == 0) {
            participant.setInitialEloPoints(user.getEloPoints());
            participantRepository.save(participant);
        }

        currentEloMap.putIfAbsent(participant.getId(), participant.getInitialEloPoints());
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
        int whiteElo = currentEloMap.getOrDefault(participant1.getId(), 1200); // начальный Elo, если не установлен
        int blackElo = currentEloMap.getOrDefault(participant2.getId(), 1200);

        boolean bothUsersAuthorized = SecurityUtil.isAuthorizedUser(participant1.getUser()) && SecurityUtil.isAuthorizedUser(participant2.getUser());

        EloUpdateResultDto updateResult = eloCalculationStrategy.calculateElo(whiteElo, blackElo, match.getResult(), bothUsersAuthorized);

        int whiteEloChange = updateResult.getWhiteNewElo() - whiteElo;
        int blackEloChange = updateResult.getBlackNewElo() - blackElo;


        currentEloMap.put(participant1.getId(), whiteElo + whiteEloChange);
        currentEloMap.put(participant2.getId(), blackElo + blackEloChange);

        participant1.setFinalEloPoints(participant1.getFinalEloPoints() + whiteEloChange);
        participant2.setFinalEloPoints(participant2.getFinalEloPoints() + blackEloChange);

        participantRepository.save(participant1);
        participantRepository.save(participant2);
    }


    private void finalizeTournament(TournamentEntity tournament, Map<UUID, Integer> currentEloMap) {
        for (Map.Entry<UUID, Integer> entry : currentEloMap.entrySet()) {
            ParticipantEntity participant = participantRepository.findById(entry.getKey()).orElse(null);
            if (participant != null) {
                UserEntity user = participant.getUser();

                if (SecurityUtil.isAuthorizedUser(user)) {
                    int newElo = user.getEloPoints() + participant.getFinalEloPoints();
                    user.setEloPoints(newElo);
                    userRepository.save(user);
                }
            }
        }

        tournament.setHasEloCalculated(true);
        tournamentRepository.save(tournament);
    }

    @Override
    public void rollbackEloChanges(TournamentEntity tournament) {

        if (!eloServiceEnabled) {
            System.out.println("Elo Service is disabled, rollback skipped.");
            return;
        }

        Set<ParticipantEntity> uniqueParticipants = tournament.getRounds().stream()
                .flatMap(round -> round.getMatches().stream())
                .flatMap(match -> Stream.of(match.getParticipant1(), match.getParticipant2()))
                .filter(participant -> participant != null && participant.getUser() != null)  // Фильтруем только участников с пользователями
                .collect(Collectors.toSet());

        for (ParticipantEntity participant : uniqueParticipants) {
            UserEntity user = participant.getUser();

            if (participant.getFinalEloPoints() != 0) {
                int originalElo = user.getEloPoints() - participant.getFinalEloPoints();
                user.setEloPoints(originalElo);
                userRepository.save(user);
            }

            participant.setFinalEloPoints(0);
            participantRepository.save(participant);  // Сохраняем изменения для участника

        }

        tournament.setHasEloCalculated(false);
        tournamentRepository.save(tournament);
    }
}
