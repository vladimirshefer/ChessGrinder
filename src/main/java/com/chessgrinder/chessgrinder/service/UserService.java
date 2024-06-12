package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.UserDto;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.exceptions.UserNotFoundException;
import com.chessgrinder.chessgrinder.mappers.UserMapper;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.CustomOAuth2User;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.chessgrinder.chessgrinder.util.DateUtil.nowInstantAtUtc;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getAllUsers(LocalDateTime globalScoreFromDate, LocalDateTime globalScoreToDate) {
        List<UserEntity> users = userRepository.findAll();
        calculateGlobalScore(users, globalScoreFromDate, globalScoreToDate);

        return users.stream().map(userMapper::toDto)
                .sorted(Comparator.comparing(UserDto::getGlobalScore, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(UserDto::getReputation, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    public void calculateGlobalScore(
            UserEntity user,
            @Nullable
            LocalDateTime globalScoreFromDate,
            @Nullable
            LocalDateTime globalScoreToDate
    ) {
        if (globalScoreFromDate == null) {
            globalScoreFromDate = LocalDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
        }

        if (globalScoreToDate == null) {
            globalScoreToDate = LocalDateTime.ofInstant(nowInstantAtUtc(), ZoneOffset.UTC);
        }

        BigDecimal userGlobalScore = userRepository.getGlobalScore(
                user.getId(),
                globalScoreFromDate,
                globalScoreToDate
        );

        user.setGlobalScore(userGlobalScore);
    }

    public void calculateGlobalScore(UserEntity user) {
        calculateGlobalScore(user, null, null);
    }

    public void calculateGlobalScore(
            List<UserEntity> users,
            @Nullable
            LocalDateTime startSeasonDate,
            @Nullable
            LocalDateTime endSeasonDate
    ) {
        users.forEach(u -> calculateGlobalScore(u, startSeasonDate, endSeasonDate));
    }

    public UserDto getUserByUserId(String userId) {

        UserEntity user = userRepository.findById(UUID.fromString(userId)).orElse(null);

        if (user == null) {
            log.error("There is no such user with id: '" + userId + "'");
            throw new UserNotFoundException("There is no such user with id: '" + userId + "'");
        }
        calculateGlobalScore(user);
        return userMapper.toDto(user);

    }

    public UserDto getUserByUserName(String userName) {
        UserEntity user = userRepository.findByUsername(userName);

        if (user == null) {
            log.error("There is no such user with username: '" + userName + "'");
            throw new UserNotFoundException("There is no such user with username: '" + userName + "'");
        }
        calculateGlobalScore(user);
        return userMapper.toDto(user);
    }

    @Nonnull
    public UserEntity findUserByIdOrUsername(String userIdOrUsername) {
        UserEntity user = userRepository.findByUsername(userIdOrUsername);
        if (user == null) {
            UUID userId;
            try {
                userId = UUID.fromString(userIdOrUsername);
            } catch (IllegalArgumentException e) {
                throw new UserNotFoundException("No user with id " + userIdOrUsername, e);
            }
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("No user with id " + userIdOrUsername));
        }
        return user;
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
