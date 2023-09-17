package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.exceptions.*;
import com.chessgrinder.chessgrinder.mappers.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BadgeMapper badgeMapper;
    private final BadgeRepository badgeRepository;

    public List<MemberDto> getAllUsers() {
        var users = userRepository.findAll();
        return users.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    public MemberDto getUserById(UUID userId) throws UserNotFoundException {

        return userRepository.findById(userId).map(user -> {

            List<BadgeDto> badges = badgeMapper.toDto(badgeRepository.getAllBadgesByUserId(user.getId()));

            return MemberDto.builder()
                    .id(userId.toString())
                    .username(user.getUsername())
                    .name(user.getName())
                    .badges(badges)
                    .build();

        }).orElseThrow(UserNotFoundException::new); //TODO log userID;
    }

    public MemberDto getUserByUserName(String userName) throws UserNotFoundException  {

        return userRepository.findByUsername(userName).map(user -> {

            List<BadgeDto> badges = badgeMapper.toDto(badgeRepository.getAllBadgesByUserId(user.getId()));

            return MemberDto.builder()
                    .id(user.getId().toString())
                    .username(userName)
                    .name(user.getName())
                    .badges(badges)
                    .build();
        }).orElseThrow(UserNotFoundException::new);
    }
}
