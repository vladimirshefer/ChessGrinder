package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.exceptions.UserNotFoundException;
import com.chessgrinder.chessgrinder.mappers.UserMapper;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ParticipantRepository participantRepository;

    public List<UserDto> getAllUsers(Date startSeasonDate, Date endSeasonDate) {
        List<UserEntity> users = userRepository.findAll();
        calcPointsPerUser(users, startSeasonDate, endSeasonDate);

        return users.stream().map(userMapper::toDto)
                .sorted(Comparator.comparing(UserDto::getTotalPoints)
                        .thenComparing(UserDto::getReputation).reversed())
                .collect(Collectors.toList());
    }

    public void calcPointsPerUser(UserEntity user, Date startSeasonDate, Date endSeasonDate) {
        final var userId = user.getId();
        final List<ParticipantEntity> participants = participantRepository.findAllByUserId(userId);
        final boolean areDatesNull = startSeasonDate == null || endSeasonDate == null;
        final var totalPoints = participants.stream()
                .filter(p -> {
                    final var tournament = p.getTournament();
                    if (tournament.getStatus() != TournamentStatus.FINISHED) {
                        return false;
                    }
                    if (areDatesNull) {
                        return true;
                    }
                    final LocalDateTime tournamentDateTime = tournament.getDate().toLocalDate().atStartOfDay();
                    //date without time
                    final Date tournamentDate = Date.from(tournamentDateTime.atZone(ZoneId.systemDefault()).toInstant());
                    return !tournamentDate.before(startSeasonDate) && !tournamentDate.after(endSeasonDate);
                })
                .map(ParticipantEntity::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        user.setTotalPoints(totalPoints);
    }

    public void calcPointsPerUser(UserEntity user) {
        calcPointsPerUser(user, null, null);
    }

    public void calcPointsPerUser(List<UserEntity> users) {
        users.forEach(this::calcPointsPerUser);
    }

    public void calcPointsPerUser(List<UserEntity> users, Date startSeasonDate, Date endSeasonDate) {
        users.forEach(u -> calcPointsPerUser(u, startSeasonDate, endSeasonDate));
    }

    public UserDto getUserByUserId(String userId) {

        UserEntity user = userRepository.findById(UUID.fromString(userId)).orElse(null);

        if (user == null) {
            log.error("There is no such user with id: '" + userId + "'");
            throw new UserNotFoundException("There is no such user with id: '" + userId + "'");
        }
        calcPointsPerUser(user);
        return userMapper.toDto(user);

    }

    public UserDto getUserByUserName(String userName) {

        UserEntity user = userRepository.findByUsername(userName);

        if (user == null) {
            log.error("There is no such user with username: '" + userName + "'");
            throw new UserNotFoundException("There is no such user with username: '" + userName + "'");
        }
        calcPointsPerUser(user);
        return userMapper.toDto(user);
    }

    public void processOAuthPostLogin(CustomOAuth2User oAuth2User) {
        UserEntity user = userRepository.findByUsername(oAuth2User.getEmail());

        if (user == null) {
            UserEntity newUser = new UserEntity();
            newUser.setUsername(oAuth2User.getEmail());
            newUser.setName(oAuth2User.getFullName());
            newUser.setProvider(UserEntity.Provider.GOOGLE);
            user = userRepository.save(newUser);
        }

        oAuth2User.setUser(user);
    }
}
