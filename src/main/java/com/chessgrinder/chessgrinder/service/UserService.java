package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.StatsAgainstUserDTO;
import com.chessgrinder.chessgrinder.dto.UserDto;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.exceptions.UserNotFoundException;
import com.chessgrinder.chessgrinder.mappers.UserMapper;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;

    @Value("${chessgrinder.security.adminEmail:}")
    private String adminEmail = "";

    public List<UserDto> getAllUsers(@Nullable Integer limit, @Nullable String city) {
        List<UserEntity> users;
        if (limit == null || limit <= 0) {
            limit = 1000;
        }
        if (city != null && !city.isBlank()) {
            users = userRepository.findAllOrderedByCity(city, Pageable.ofSize(limit).withPage(0)).getContent();
        } else {
            users = userRepository.findAllOrdered(Pageable.ofSize(limit).withPage(0)).getContent();
        }

        return users.stream().map(userMapper::toDto)
                .sorted(Comparator
                        .comparing(
                                UserDto::getEloPoints,
                                Comparator.nullsLast(Comparator.reverseOrder())
                        )
                        .thenComparing(
                                UserDto::getReputation,
                                Comparator.nullsLast(Comparator.reverseOrder())
                        )
                )
                .collect(Collectors.toList());
    }

    public UserDto getUserByUserId(String userId) {

        UserEntity user = userRepository.findById(UUID.fromString(userId)).orElse(null);

        if (user == null) {
            log.error("There is no such user with id: '" + userId + "'");
            throw new UserNotFoundException("There is no such user with id: '" + userId + "'");
        }
        return userMapper.toDto(user);

    }

    public UserDto getUserByUsertag(String usertag) {
        UserEntity user = userRepository.findByUsertag(usertag);

        if (user == null) {
            log.error("There is no such user with usertag: '" + usertag + "'");
            throw new UserNotFoundException("There is no such user with usertag: '" + usertag + "'");
        }

        return userMapper.toDto(user);
    }


    public StatsAgainstUserDTO getStatsAgainstUser(UUID authUserId, UUID opponentUserId) {
        final var statsAgainstOpponent = userRepository.getStatsAgainstUser(authUserId, opponentUserId);
        //Getting row in result
        final var statsRow = statsAgainstOpponent.get(0);
        //Getting columns in selected row
        return StatsAgainstUserDTO.builder()
                .wins(statsRow[0])
                .losses(statsRow[1])
                .draws(statsRow[2])
                .build();
    }
}
