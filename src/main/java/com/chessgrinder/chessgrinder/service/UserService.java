package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.exceptions.UserNotFoundException;
import com.chessgrinder.chessgrinder.mappers.UserMapper;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.CustomOAuth2User;
import com.chessgrinder.chessgrinder.utils.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private static final String DATE_FORMAT_STRING = "dd.MM.yyyy";

    public List<UserDto> getAllUsers(String startSeasonDateString, String endSeasonDateString) {
        final Pair<Date, Date> dates = getSeasonDates(startSeasonDateString, endSeasonDateString);
        List<UserEntity> users = userRepository.findAll();

        return users.stream().map(user -> userMapper.toDto(user, dates.getFirst(), dates.getSecond()))
                .sorted(Comparator.comparing(UserDto::getTotalPoints)
                        .thenComparing(UserDto::getReputation).reversed())
                .collect(Collectors.toList());
    }

    private static Pair<Date, Date> getSeasonDates(String startSeasonDateString, String endSeasonDateString) {
        Date startSeasonDate = null;
        Date endSeasonDate = null;
        try {
            if (startSeasonDateString != null) {
                startSeasonDate = getDateFromString(startSeasonDateString);
            }
            if (endSeasonDateString != null) {
                endSeasonDate = getDateFromString(endSeasonDateString);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(400, "Can't parse start or end season date with format " + DATE_FORMAT_STRING, e);
        }
        if (startSeasonDate != null && endSeasonDate != null && endSeasonDate.before(startSeasonDate)) {
            throw new ResponseStatusException(400, "End date can't be before start date", null);
        }
        return new Pair<>(startSeasonDate, endSeasonDate);
    }

    private static Date getDateFromString(String dateString) {
        LocalDateTime localDateTime = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_FORMAT_STRING)).atStartOfDay();
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public UserDto getUserByUserId(String userId) {

        UserEntity user = userRepository.findById(UUID.fromString(userId)).orElse(null);

        if (user == null) {
            log.error("There is no such user with id: '" + userId + "'");
            throw new UserNotFoundException("There is no such user with id: '" + userId + "'");
        }

        return userMapper.toDto(user);

    }

    public UserDto getUserByUserName(String userName) {

        UserEntity user = userRepository.findByUsername(userName);

        if (user == null) {
            log.error("There is no such user with username: '" + userName + "'");
            throw new UserNotFoundException("There is no such user with username: '" + userName + "'");
        }

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
