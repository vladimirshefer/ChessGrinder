package com.chessgrinder.chessgrinder.service;

import java.util.*;
import java.util.stream.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.exceptions.*;
import com.chessgrinder.chessgrinder.mappers.*;
import com.chessgrinder.chessgrinder.repositories.*;
import com.chessgrinder.chessgrinder.security.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BadgeMapper badgeMapper;
    private final BadgeRepository badgeRepository;
    private final RoleRepository roleRepository;

    public List<MemberDto> getAllUsers() {
        var users = userRepository.findAll();
        return users.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    public MemberDto getUserByUserId(String userId) {

        UserEntity user = userRepository.findById(UUID.fromString(userId)).orElse(null);

        if (user == null) {
            log.error("There is no such user with id: '" + userId + "'");
            throw new UserNotFoundException("There is no such user with id: '" + userId + "'");
        }

        return userMapper.toDto(user);

    }

    public MemberDto getUserByUserName(String userName) {

        UserEntity user = userRepository.findByUsername(userName);

        if (user == null) {
            log.error("There is no such user with username: '" + userName + "'");
            throw new UserNotFoundException("There is no such user with username: '" + userName + "'");
        }

        return userMapper.toDto(user);
    }

    public void processOAuthPostLogin(CustomOAuth2User username) {
        UserEntity existUser = userRepository.findByUsername(username.getEmail());

        if (existUser == null) {
            UserEntity newUser = new UserEntity();
            newUser.setUsername(username.getEmail());
            newUser.setName(username.getName());
            newUser.setProvider(UserEntity.Provider.GOOGLE);

            if (List.of("quameu@gmail.com", "al.boldyrev1@gmail.com").contains(username.getEmail())) {
                RoleEntity adminRole = roleRepository.findByName(RoleEntity.Roles.ADMIN)
                        .orElseGet(() -> roleRepository.save(RoleEntity.builder().name(RoleEntity.Roles.ADMIN).build()));
                newUser.setRoles(List.of(adminRole));
            }

            userRepository.save(newUser);
        }

    }
}
