package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.StatsAgainstUserDTO;
import com.chessgrinder.chessgrinder.dto.UserDto;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.exceptions.UserNotFoundException;
import com.chessgrinder.chessgrinder.mappers.UserMapper;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.principal.CustomOAuth2User;
import com.chessgrinder.chessgrinder.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
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

    public List<UserDto> getAllUsers(Integer limit) {
        List<UserEntity> users;
        if (limit != null && limit > 0) {
            users = userRepository.findAllOrdered(Pageable.ofSize(limit).withPage(0)).getContent();
        } else {
            users = userRepository.findAllOrdered();
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

    public UserDto getUserByUserName(String userName) {
        UserEntity user = userRepository.findByUsername(userName);

        if (user == null) {
            log.error("There is no such user with username: '" + userName + "'");
            throw new UserNotFoundException("There is no such user with username: '" + userName + "'");
        }

        return userMapper.toDto(user);
    }

    public void processOAuthPostLogin(CustomOAuth2User oAuth2User) {
        String email = oAuth2User.getEmail().toLowerCase();
        UserEntity user = userRepository.findByUsername(email);


        if (user == null) {
            UserEntity newUser = new UserEntity();
            newUser.setUsername(email);
            newUser.setName(oAuth2User.getFullName());
            newUser.setProvider(UserEntity.Provider.GOOGLE);
            user = userRepository.save(newUser);
        }

        {
            Set<String> adminEmails = Arrays.stream(adminEmail.split(","))
                    .filter(StringUtils::isNotBlank)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

            if (adminEmails.contains(email)) {
                if (!SecurityUtil.hasRole(user, RoleEntity.Roles.ADMIN)) {
                    roleService.assignRole(user, RoleEntity.Roles.ADMIN);
                }

                user = userRepository.findById(user.getId()).orElseThrow();
            }
        }

        oAuth2User.setUser(user);
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
