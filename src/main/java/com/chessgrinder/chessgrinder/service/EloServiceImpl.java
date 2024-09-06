package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.EloUpdateResultDto;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Service
public class EloServiceImpl implements EloService {

    private final UserEloInitializerService userEloInitializerService;
    private final DefaultEloCalculationStrategy defaultEloCalculationStrategy;
    private final ParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;

    @Autowired
    public EloServiceImpl(UserEloInitializerService userEloInitializerService,
                          DefaultEloCalculationStrategy defaultEloCalculationStrategy,
                          ParticipantRepository participantRepository,
                          UserRepository userRepository,
                          TournamentRepository tournamentRepository) {
        this.userEloInitializerService = userEloInitializerService;
        this.defaultEloCalculationStrategy = defaultEloCalculationStrategy;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    @Transactional
    public void processTournamentAndUpdateElo(TournamentEntity tournament) {

        Map<UUID, Integer> currentEloMap = new HashMap<>();

        // Основной цикл для инициализации ELO и пересчета на основе результатов матчей
        for (RoundEntity round : tournament.getRounds()) {
            for (MatchEntity match : round.getMatches()) {
                ParticipantEntity participant1 = match.getParticipant1();
                ParticipantEntity participant2 = match.getParticipant2();

                if (participant1 == null || participant2 == null) {
                    continue; // Пропускаем, если одного из участников нет
                }

                UserEntity user1 = participant1.getUser();
                UserEntity user2 = participant2.getUser();

                if (user1 != null) {
                    currentEloMap.putIfAbsent(participant1.getId(), user1.getEloPoints());
                }

                if (user2 != null) {
                    currentEloMap.putIfAbsent(participant2.getId(), user2.getEloPoints());
                }

                boolean isUser1Authorized = SecurityUtil.isAuthorizedUser(user1);
                boolean isUser2Authorized = SecurityUtil.isAuthorizedUser(user2);

                if (!isUser1Authorized && !isUser2Authorized) {
                    continue; // Оба пользователя неавторизованы, пропускаем
                }

                userEloInitializerService.setDefaultEloIfNeeded(user1, isUser1Authorized);
                userEloInitializerService.setDefaultEloIfNeeded(user2, isUser2Authorized);

                setInitialAndTemporaryElo(participant1, user1, isUser1Authorized, currentEloMap);
                setInitialAndTemporaryElo(participant2, user2, isUser2Authorized, currentEloMap);

                int player1Elo = currentEloMap.getOrDefault(participant1.getId(), 0);
                int player2Elo = currentEloMap.getOrDefault(participant2.getId(), 0);

                boolean bothUsersAuthorized = isUser1Authorized && isUser2Authorized;

                EloUpdateResultDto updateResult = defaultEloCalculationStrategy.calculateElo(
                        player1Elo, player2Elo, match.getResult(), bothUsersAuthorized);

                int updatedEloFirst = updateResult.getWhiteNewElo();
                int updatedEloSecond = updateResult.getBlackNewElo();

                currentEloMap.put(participant1.getId(), updatedEloFirst);
                currentEloMap.put(participant2.getId(), updatedEloSecond);

                updateEloPoints(user1, updatedEloFirst, isUser1Authorized);
                updateEloPoints(user2, updatedEloSecond, isUser2Authorized);
            }
        }

        // Сохранение состояния турнира после всех расчетов
        tournament.setHasEloCalculated(true);
        tournamentRepository.save(tournament);
    }

    private void setInitialAndTemporaryElo(ParticipantEntity participant, UserEntity user, boolean isAuthorized, Map<UUID, Integer> temporaryEloMap) {
        if (isAuthorized && participant.getInitialEloPoints() == 0) {
            participant.setInitialEloPoints(user.getEloPoints());
            temporaryEloMap.put(participant.getId(), user.getEloPoints());
            participantRepository.save(participant);
        }
    }

    private void updateEloPoints(UserEntity user, int newElo, boolean isAuthorized) {
        if (isAuthorized) {
            user.setEloPoints(newElo);
            userRepository.save(user);
        }
    }

    @Override
    public void rollbackEloChanges(TournamentEntity tournament) {
        // Логика для отката изменений Elo, если турнир изменился после подсчета
        List<MatchEntity> matches = tournament.getRounds().stream()
                .flatMap(round -> round.getMatches().stream())
                .toList();

        for (MatchEntity match : matches) {
            ParticipantEntity participant1 = match.getParticipant1();
            ParticipantEntity participant2 = match.getParticipant2();

            if (participant1 != null) {
                UserEntity user1 = participant1.getUser();
                if (user1 != null && participant1.getInitialEloPoints() > 0) {
                    user1.setEloPoints(participant1.getInitialEloPoints());
                    userRepository.save(user1);
                }
            }
            if (participant2 != null) {
                UserEntity user2 = participant2.getUser();
                if (user2 != null && participant2.getInitialEloPoints() > 0) {
                    user2.setEloPoints(participant2.getInitialEloPoints());
                    userRepository.save(user2);
                }
            }
        }

        tournament.setHasEloCalculated(false);
        tournamentRepository.save(tournament);
    }
}