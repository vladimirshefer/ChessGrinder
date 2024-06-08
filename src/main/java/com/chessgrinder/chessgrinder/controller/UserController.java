package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.exceptions.UserNotFoundException;
import com.chessgrinder.chessgrinder.mappers.ParticipantMapper;
import com.chessgrinder.chessgrinder.mappers.SubscriptionMapper;
import com.chessgrinder.chessgrinder.mappers.TournamentMapper;
import com.chessgrinder.chessgrinder.mappers.UserMapper;
import com.chessgrinder.chessgrinder.repositories.*;
import com.chessgrinder.chessgrinder.security.AuthenticatedUserArgumentResolver.AuthenticatedUser;
import com.chessgrinder.chessgrinder.service.UserService;
import com.chessgrinder.chessgrinder.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Nullable;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final SubscriptionLevelRepository subscriptionLevelRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
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
    private static final String DATE_FORMAT_STRING = "dd.MM.yyyy";

    @GetMapping
    public ListDto<UserDto> getUsers(
            @Nullable
            @RequestParam(required = false)
            @DateTimeFormat(pattern = DATE_FORMAT_STRING)
            LocalDate globalScoreFromDate,
            @Nullable
            @RequestParam(required = false)
            @DateTimeFormat(pattern = DATE_FORMAT_STRING)
            LocalDate globalScoreToDate
    ) {
        if (globalScoreFromDate != null && globalScoreToDate != null && globalScoreToDate.isBefore(globalScoreFromDate)) {
            throw new ResponseStatusException(400, "End date can't be before start date", null);
        }
        final List<UserDto> allUsers = userService.getAllUsers(
                DateUtil.atStartOfDay(globalScoreFromDate),
                DateUtil.atStartOfDay(globalScoreToDate)
        );
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
        userService.calculateGlobalScore(authenticatedUser);
        return userMapper.toDto(authenticatedUser);
    }

    @GetMapping("/{userIdOrUsername}/history")
    public ListDto<UserHistoryRecordDto> history(
            @PathVariable String userIdOrUsername
    ) {
        UserEntity user = userService.findUserByIdOrUsername(userIdOrUsername);
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

    @PatchMapping("/{userId}")
    public void updateUser(
            @PathVariable UUID userId,
            @RequestBody UserDto userDto,
            @AuthenticatedUser UserEntity authenticatedUser
    ) {
        if (!userId.equals(authenticatedUser.getId())) {
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
            boolean isDeleterAdmin = authenticatedUser.getRoles().stream().anyMatch(it -> it.getName().equals(RoleEntity.Roles.ADMIN));
            final var deletedUser = userRepository.findById(userId).orElseThrow();
            boolean isDeletedAdmin = deletedUser.getRoles().stream().anyMatch(it -> it.getName().equals(RoleEntity.Roles.ADMIN));
            if (!isDeleterAdmin || isDeletedAdmin) {
                throw new ResponseStatusException(403, "Not allowed to delete other's profile", null);
            }
        }

        userRepository.deleteById(userId);
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

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping("/{userId}/subscription")
    public void assignSubscription(
            @PathVariable UUID userId,
            @RequestBody SubscriptionDto data
    ) {
        UserEntity user = userRepository.findById(userId).orElseThrow();
        ClubEntity club = clubRepository.getById(UUID.fromString(data.getClub().getId()));
        SubscriptionLevelEntity subscriptionLevel = subscriptionLevelRepository.findByName(
                data.getSubscriptionLevel().getName()).orElseThrow();
        final var subscription = SubscriptionEntity.builder()
                .club(club)
                .subscriptionLevel(subscriptionLevel)
                .startDate(Instant.now(Clock.systemUTC()))
                .finishDate(data.getFinishDate())
                .user(user)
                .build();

        subscriptionRepository.save(subscription);
    }

    @GetMapping("/{userId}/subscription")
    public ListDto<SubscriptionDto> getUserSubscriptions(
            @PathVariable UUID userId
    ) {
        final var allSubscriptions = subscriptionRepository.findAllByUserId(userId);
        return ListDto.<SubscriptionDto>builder().values(subscriptionMapper.toDto(allSubscriptions)).build();
    }
}
