package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.BadgeDto;
import com.chessgrinder.chessgrinder.dto.MemberDto;
import com.chessgrinder.chessgrinder.entities.User;
import com.chessgrinder.chessgrinder.exceptions.UserNotFoundException;
import com.chessgrinder.chessgrinder.mappers.BadgeMapper;
import com.chessgrinder.chessgrinder.mappers.UserMapper;
import com.chessgrinder.chessgrinder.repositories.BadgeRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BadgeMapper badgeMapper;
    private final BadgeRepository badgeRepository;

    public List<MemberDto> getAllUsers() {
        var users = userRepository.findAll();
        return users.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    public MemberDto getUserByUserId(String userId) {

        User user = userRepository.findById(UUID.fromString(userId)).orElse(null);

        if (user == null) {
            log.error("There is no such user with id: '" + userId + "'");
            throw new UserNotFoundException("There is no such user with id: '" + userId + "'");
        }
        List<BadgeDto> badges = badgeMapper.toDto(badgeRepository.getAllBadgesByUserId(user.getId()));

        return MemberDto.builder()
                .id(userId)
                .username(user.getUsername())
                .name(user.getName())
                .badges(badges)
                .build();

    }

    public MemberDto getUserByUserName(String userName) {

        User user = userRepository.findByUsername(userName);

        if (user == null) {
            log.error("There is no such user with username: '" + userName + "'");
            throw new UserNotFoundException("There is no such user with username: '" + userName + "'");
        }
        List<BadgeDto> badges = badgeMapper.toDto(badgeRepository.getAllBadgesByUserId(user.getId()));

        return MemberDto.builder()
                    .id(user.getId().toString())
                    .username(userName)
                    .name(user.getName())
                    .badges(badges)
                    .build();

    }

    public void processOAuthPostLogin(CustomOAuth2User username) {
        User existUser = userRepository.findByUsername(username.getEmail());

        if (existUser == null) {
            User newUser = new User();
            newUser.setUsername(username.getEmail());
            newUser.setName(username.getName());
            newUser.setProvider(User.Provider.GOOGLE);

            userRepository.save(newUser);
        }

    }
}
