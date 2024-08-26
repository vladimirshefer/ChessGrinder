package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EloServiceImpl implements EloService {

    private static final int WIN_POINTS = 10;
    private static final int LOSE_POINTS = -10;
    private static final int UNRATED_WIN_POINTS = 5;
    private static final int UNRATED_LOSE_POINTS = -5;



    @Autowired
    private UserRepository userRepository;

    @Override
    public void updateElo(MatchEntity match) {
        ParticipantEntity participant1 = match.getParticipant1();
        ParticipantEntity participant2 = match.getParticipant2();

        if (participant1 == null || participant2 == null) {
            return; // Если одного из участников нет, обновление рейтинга не требуется.
        }

        UserEntity user1 = participant1.getUser();
        UserEntity user2 = participant2.getUser();

        // Определяем, являются ли оба пользователя авторизованными
        boolean isUser1Authorized = user1 != null && user1.getId() != null;
        boolean isUser2Authorized = user2 != null && user2.getId() != null;

        // Пропускаем все итерации для неавторизованных пользователей
        if (!isUser1Authorized && !isUser2Authorized) {
            return; // Оба пользователя неавторизованы, ничего не делаем
        }

        // Инициализируем временные рейтинги только для авторизованных пользователей
        if (isUser1Authorized && participant1.getInitialEloPoints() == 0) {
            participant1.setInitialEloPoints(user1.getEloPoints());
            participant1.setTemporaryEloPoints(user1.getEloPoints());
        }

        if (isUser2Authorized && participant2.getInitialEloPoints() == 0) {
            participant2.setInitialEloPoints(user2.getEloPoints());
            participant2.setTemporaryEloPoints(user2.getEloPoints());
        }


        if (match.getResult() == MatchResult.DRAW) {
            return; // В случае ничьей временные рейтинги не изменяются.
        }

        if (match.getResult() == MatchResult.WHITE_WIN) {
            updatePlayerTemporaryElo(participant1, participant2, isUser1Authorized, isUser2Authorized);
        } else if (match.getResult() == MatchResult.BLACK_WIN) {
            updatePlayerTemporaryElo(participant2, participant1, isUser2Authorized, isUser1Authorized);
        }
    }

    private void updatePlayerTemporaryElo(ParticipantEntity winner, ParticipantEntity loser, boolean isWinnerAuthorized, boolean isLoserAuthorized) {
        // Если победитель авторизован, обновляем его рейтинг
        if (isWinnerAuthorized) {
            int newElo = winner.getTemporaryEloPoints() + (isLoserAuthorized ? WIN_POINTS : UNRATED_WIN_POINTS);
            winner.setTemporaryEloPoints(newElo);
        }

        // Если проигравший авторизован, обновляем его рейтинг
        if (isLoserAuthorized) {
            int newElo = loser.getTemporaryEloPoints() + (isWinnerAuthorized ? LOSE_POINTS : UNRATED_LOSE_POINTS);
            loser.setTemporaryEloPoints(newElo);
        }
    }

    // Метод для окончательного обновления рейтинга в базе данных после турнира
    @Override
    public void finalizeEloUpdates(List<ParticipantEntity> participants) {
        for (ParticipantEntity participant : participants) {
            UserEntity user = participant.getUser();
            int initialElo = participant.getInitialEloPoints();
            int finalElo = participant.getTemporaryEloPoints();
            int eloDifference = finalElo - initialElo;

            assert user != null;
            user.setEloPoints(user.getEloPoints() + eloDifference); // Обновляем основной рейтинг
            userRepository.save(user);
        }
    }
    }


