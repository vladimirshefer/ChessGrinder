package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.exceptions.UserNotFoundException;
import com.chessgrinder.chessgrinder.mappers.UserMapper;
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

    public List<UserDto> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();

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

    public UserDto getUserByUserName(String userName) {

        UserEntity user = userRepository.findByUsername(userName);

        if (user == null) {
            log.error("There is no such user with username: '" + userName + "'");
            throw new UserNotFoundException("There is no such user with username: '" + userName + "'");
        }

        return userMapper.toDto(user);
    }

    public void processOAuthPostLogin(CustomOAuth2User oAuth2User) {
        UserEntity user = userRepository.findByEmail(oAuth2User.getEmail());

        if (user == null) {
            UserEntity newUser = new UserEntity();
            newUser.setEmail(oAuth2User.getEmail());
            newUser.setUsername(oAuth2User.getEmail());
            newUser.setName(oAuth2User.getFullName());
            newUser.setProvider(UserEntity.Provider.GOOGLE);
            user = userRepository.save(newUser);
        }

        oAuth2User.setUser(user);
    }
}
