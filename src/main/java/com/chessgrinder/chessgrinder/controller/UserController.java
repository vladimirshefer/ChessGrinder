package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.exceptions.UserNotFoundException;
import com.chessgrinder.chessgrinder.mappers.ParticipantMapper;
import com.chessgrinder.chessgrinder.mappers.TournamentMapper;
import com.chessgrinder.chessgrinder.mappers.UserMapper;
import com.chessgrinder.chessgrinder.repositories.*;
import com.chessgrinder.chessgrinder.security.AuthenticatedUserArgumentResolver.AuthenticatedUser;
import com.chessgrinder.chessgrinder.service.UserService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final TournamentMapper tournamentMapper;
    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;
    private final UserReputationHistoryRepository userReputationHistoryRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${chessgrinder.feature.auth.signupWithPasswordEnabled:true}")
    private boolean isSignupWithPasswordEnabled;

    private static final String USERNAME_REGEX = "^[a-zA-Z][a-zA-Z0-9]+$";

    @GetMapping
    public ListDto<UserDto> getUsers() {
        final var allUsers = userService.getAllUsers().stream()
                .sorted(Comparator.comparing(UserDto::getReputation).reversed())
                .collect(Collectors.toList());
        return ListDto.<UserDto>builder().values(allUsers).build();
    }

    @GetMapping("/{userId}")
    public UserDto addParticipantToTournament(@PathVariable String userId) throws UserNotFoundException {
        try {
            UserDto user = userService.getUserByUserId(userId);
            if (user != null) {
                return user;
            }
        } catch (Exception e) {

        }
        return userService.getUserByUserName(userId);
    }

    @Transactional
    @GetMapping("/me")
    public UserDto me(
            @AuthenticatedUser UserEntity authenticatedUser
    ) {
        return userMapper.toDto(authenticatedUser);
    }

    @GetMapping("/{userIdOrUsername}/history")
    public ListDto<UserHistoryRecordDto> history(
            @PathVariable String userIdOrUsername
    ) {
        UserEntity user = findUserByIdOrUsername(userIdOrUsername);
        List<ParticipantEntity> participants = participantRepository.findAllByUserId(user.getId());
        List<UserHistoryRecordDto> history = participants.stream()
                .filter(participant -> TournamentStatus.FINISHED == Optional
                        .ofNullable(participant.getTournament())
                        .map(TournamentEntity::getStatus)
                        .orElse(null))
                .sorted(Comparator.comparing((ParticipantEntity it) -> it.getTournament().getDate()).reversed())
                .map(participant ->
                        UserHistoryRecordDto.builder()
                                .tournament(tournamentMapper.toDto(participant.getTournament()))
                                .participant(participantMapper.toDto(participant))
                                .build()
                )
                .toList();
        return ListDto.<UserHistoryRecordDto>builder().values(history).build();
    }

    @Nonnull
    private UserEntity findUserByIdOrUsername(String userIdOrUsername) {
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

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping("/{userId}/badge/{badgeId}")
    public void assignBadge(
            @PathVariable UUID userId,
            @PathVariable UUID badgeId
    ) {
        UserEntity user = userRepository.findById(userId).orElseThrow();
        BadgeEntity badge = badgeRepository.findById(badgeId).orElseThrow();
        UserBadgeEntity assignment = UserBadgeEntity.builder().badge(badge).user(user).build();
        userBadgeRepository.save(assignment);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping("/{userId}/reputation")
    @Transactional
    public void assignReputation(
            @PathVariable UUID userId,
            @RequestBody UserReputationHistoryRecordDto data
    ) {
        UserEntity user = userRepository.findById(userId).orElseThrow();
        userReputationHistoryRepository.save(UserReputationHistoryEntity.builder()
                .amount(data.getAmount())
                .comment(data.getComment())
                .user(user)
                .build());
        userRepository.addReputation(userId, data.getAmount());
    }

    @PatchMapping("/{userName}")
    public void updateUser(
            @PathVariable String userName,
            @RequestBody UserDto userDto,
            @AuthenticatedUser UserEntity authenticatedUser
    ) {
        if (!userName.equals(authenticatedUser.getUsername())) {
            throw new ResponseStatusException(403, "Not allowed to change other's name", null);
        }

        authenticatedUser.setName(userDto.getName());
        userRepository.save(authenticatedUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(
            @PathVariable UUID userId,
            @AuthenticatedUser UserEntity authenticatedUser
    ) {
        if (!userId.equals(authenticatedUser.getId())) {
            throw new ResponseStatusException(403, "Not allowed to change other's profile", null);
        }
        System.out.println("TODO epta");
    }

    @PostMapping("/signUp")
    public void signUp(
            @RequestBody UserSignUpRequest signUpRequest,
            @AuthenticatedUser(required = false) UserEntity authenticatedUser
    ) {
        if (!isSignupWithPasswordEnabled) {
            throw new ResponseStatusException(400, "Sign Up with password is disabled.", null);
        }

        if (signUpRequest == null || signUpRequest.getUsername() == null || signUpRequest.getUsername().isBlank()) {
            throw new ResponseStatusException(400, "Invalid username", null);
        }

        if (signUpRequest.getUsername().contains("@")) {
            throw new ResponseStatusException(400, "Email could not be a username", null);
        }

        boolean userNameAlreadyExists = userRepository.findByUsername(signUpRequest.getUsername()) != null;
        if (authenticatedUser != null || userNameAlreadyExists) {
            throw new ResponseStatusException(400, "Already registered", null);
        }

        String password = signUpRequest.getPassword();
        if (password == null || password.isBlank() || password.length() < 4) {
            throw new ResponseStatusException(400, "Invalid password. Min 4 chars.", null);
        }

        if (!signUpRequest.getUsername().matches(USERNAME_REGEX)) {
            throw new ResponseStatusException(400, "Invalid username", null);
        }

        userRepository.save(UserEntity.builder()
                .username(signUpRequest.getUsername())
                .name(signUpRequest.getFullName())
                .password(passwordEncoder.encode(password))
                .build()
        );
    }
}
