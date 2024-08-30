package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.repositories.MatchRepository;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EloServiceImpl implements EloService {
    private static final int WIN_POINTS = 10;
    private static final int LOSE_POINTS = -10;
    private static final int UNRATED_WIN_POINTS = 5;
    private static final int UNRATED_LOSE_POINTS = -5;
    private static final int DEFAULT_ELO_POINTS = 1200;

    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;
    private final MatchRepository matchRepository;

    @Autowired
    public EloServiceImpl(UserRepository userRepository, ParticipantRepository participantRepository, MatchRepository matchRepository) {
        this.userRepository = userRepository;
        this.participantRepository = participantRepository;
        this.matchRepository = matchRepository;
    }

    // Метод для пересчета рейтингов на основе всех матчей
    @Override
    @Transactional
    public void finalizeEloUpdates(List<MatchEntity> matches) {
        for (MatchEntity match : matches) {
            ParticipantEntity participant1 = match.getParticipant1();
            ParticipantEntity participant2 = match.getParticipant2();

            if (participant1 == null || participant2 == null) {
                continue; // Пропускаем, если нет одного из участников
            }

            UserEntity user1 = participant1.getUser();
            UserEntity user2 = participant2.getUser();

            // Пропускаем все итерации для неавторизованных пользователей
            boolean isUser1Authorized = isAuthorizedUser(user1);
            boolean isUser2Authorized = isAuthorizedUser(user2);

            if (!isUser1Authorized && !isUser2Authorized) {
                continue; // Оба пользователя неавторизованы, ничего не делаем
            }

            // Ставим дефолтный рейтинг для новых пользователей с 0 рейтингом
            setDefaultEloIfNeeded(user1, isUser1Authorized);
            setDefaultEloIfNeeded(user2, isUser2Authorized);

            if (isUser1Authorized && participant1.getInitialEloPoints() == 0) {
                participant1.setInitialEloPoints(user1.getEloPoints());
                participant1.setTemporaryEloPoints(user1.getEloPoints());
                participantRepository.save(participant1);
            }

            if (isUser2Authorized && participant2.getInitialEloPoints() == 0) {
                participant2.setInitialEloPoints(user2.getEloPoints());
                participant2.setTemporaryEloPoints(user2.getEloPoints());
                participantRepository.save(participant2);
            }

            // Проверяем, был ли уже рассчитан рейтинг для этого матча
            if (match.getHasEloCalculated() == null || match.getHasEloCalculated() == false) {

                int initialElo1 = isUser1Authorized ? participant1.getTemporaryEloPoints() : 0;
                int initialElo2 = isUser2Authorized ? participant2.getTemporaryEloPoints() : 0;


                // Рассчитываем изменение рейтинга для каждого участника на основе всех матчей
                if (match.getResult() == MatchResult.WHITE_WIN) {
                    if (isUser1Authorized) {
                        initialElo1 += isUser2Authorized ? WIN_POINTS : UNRATED_WIN_POINTS;
                    }
                    if (isUser2Authorized) {
                        initialElo2 += isUser1Authorized ? LOSE_POINTS : UNRATED_LOSE_POINTS;
                    }
                } else if (match.getResult() == MatchResult.BLACK_WIN) {
                    if (isUser2Authorized) {
                        initialElo2 += isUser1Authorized ? WIN_POINTS : UNRATED_WIN_POINTS;
                    }
                    if (isUser1Authorized) {
                        initialElo1 += isUser2Authorized ? LOSE_POINTS : UNRATED_LOSE_POINTS;
                    }
                }

                // Если ничья, изменения не происходит

                // Сохраняем обновленные рейтинги
                if (isUser1Authorized) {
                    participant1.setTemporaryEloPoints(initialElo1);
                }

                if (isUser2Authorized) {
                    participant2.setTemporaryEloPoints(initialElo2);
                }

                updateEloPoints(user1, initialElo1, isUser1Authorized);
                updateEloPoints(user2, initialElo2, isUser2Authorized);

                match.setHasEloCalculated(true);
                // Здесь нужно сохранить матч, чтобы флаг был сохранен в базе данных.
                matchRepository.save(match);
            }
        }

    }

    private boolean isAuthorizedUser(UserEntity user) {
        return user != null && user.getId() != null;
    }

    private void setDefaultEloIfNeeded(UserEntity user, boolean isAuthorized) {
        if (isAuthorized && user.getEloPoints() == 0) {
            user.setEloPoints(DEFAULT_ELO_POINTS);
            userRepository.save(user);
        }
    }

    private void updateEloPoints(UserEntity user, int newElo, boolean isAuthorized) {
        if (isAuthorized) {
            user.setEloPoints(newElo);
            userRepository.save(user);
        }
    }
}

