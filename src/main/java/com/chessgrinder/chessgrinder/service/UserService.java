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

    public List<UserDto> getAllUsers(@Nullable Integer page,
                                     @Nullable Integer size,
                                     @Nullable String sort,
                                     @Nullable String city) {
        int pageNum = (page == null || page < 0) ? 0 : page;
        int pageSize = (size == null || size <= 0) ? 50 : size;

        List<UserEntity> users;
        if (city != null && !city.isBlank()) {
            if ("reputation".equalsIgnoreCase(sort)) {
                users = userRepository.findAllByCityOrderedByReputation(city, Pageable.ofSize(pageSize).withPage(pageNum)).getContent();
            } else { // default rating
                users = userRepository.findAllOrderedByCity(city, Pageable.ofSize(pageSize).withPage(pageNum)).getContent();
            }
        } else {
            if ("reputation".equalsIgnoreCase(sort)) {
                users = userRepository.findAllOrderedByReputation(Pageable.ofSize(pageSize).withPage(pageNum)).getContent();
            } else { // default rating
                users = userRepository.findAllOrdered(Pageable.ofSize(pageSize).withPage(pageNum)).getContent();
            }
        }

        return users.stream().map(userMapper::toDto).collect(Collectors.toList());
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
