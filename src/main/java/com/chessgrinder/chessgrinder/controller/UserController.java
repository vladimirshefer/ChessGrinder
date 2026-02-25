package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.exceptions.UserNotFoundException;
import com.chessgrinder.chessgrinder.mappers.ParticipantMapper;
import com.chessgrinder.chessgrinder.mappers.UserMapper;
import com.chessgrinder.chessgrinder.repositories.*;
import com.chessgrinder.chessgrinder.security.AuthenticatedUserArgumentResolver.AuthenticatedUser;
import com.chessgrinder.chessgrinder.security.CustomPermissionEvaluator;
import com.chessgrinder.chessgrinder.security.util.SecurityUtil;
import com.chessgrinder.chessgrinder.service.UserService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;
    private final UserReputationHistoryRepository userReputationHistoryRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CustomPermissionEvaluator permissionEvaluator;

    @Value("${chessgrinder.feature.auth.password:false}")
    private boolean isSignupWithPasswordEnabled;

    @GetMapping
    public ListDto<UserDto> getUsers(
            @Nullable
            @RequestParam(required = false)
            Integer limit,
            @Nullable
            @RequestParam(required = false)
            String city
    ) {
        return ListDto.of(userService.getAllUsers(limit, city));
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
        // If not a valid UUID, treat the path value as a public usertag
        return userService.getUserByUsertag(userId);
    }

    @Transactional
    @GetMapping("/me")
    public UserDto me(
            @AuthenticatedUser(required = false)
            @Nullable
            UserEntity authenticatedUser
    ) {
        if (authenticatedUser == null) {
            return null;
        }

        return userMapper.toDto(authenticatedUser);
    }

    @GetMapping("/{userId}/participant")
    public ListDto<ParticipantDto> history2(
            @PathVariable UUID userId
    ) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with id " + userId));
        List<ParticipantEntity> participants = participantRepository.findAllByUserId(user.getId());
        return ListDto.<ParticipantDto>builder().values(participantMapper.toDto(participants)).build();
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

        // Update full name if provided
        if (userDto.getName() != null) {
            authenticatedUser.setName(userDto.getName());
        }

        // Allow updating public usertag (handle) if provided
        if (userDto.getUsertag() != null) {
            String newUsertag = userDto.getUsertag();

            if (!newUsertag.matches(UserEntity.USERTAG_REGEX)) {
                throw new ResponseStatusException(400, "Invalid username", null);
            }

            UserEntity existing = userRepository.findByUsertag(newUsertag);
            if (existing != null && !existing.getId().equals(authenticatedUser.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken", null);
            }

            authenticatedUser.setUsertag(newUsertag);
        }

        userRepository.save(authenticatedUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(
            @PathVariable UUID userId,
            @AuthenticatedUser UserEntity authenticatedUser
    ) {
        if (!userId.equals(authenticatedUser.getId())) {
            boolean isDeleterAdmin = SecurityUtil.hasRole(authenticatedUser, RoleEntity.Roles.ADMIN);
            final var deletedUser = userRepository.findById(userId).orElseThrow();
            boolean isDeletedAdmin = SecurityUtil.hasRole(deletedUser, RoleEntity.Roles.ADMIN);
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

        if (!signUpRequest.getUsername().matches(UserEntity.USERTAG_REGEX)) {
            throw new ResponseStatusException(400, "Invalid username", null);
        }

        userRepository.save(UserEntity.builder()
                .username(signUpRequest.getUsername())
                .name(signUpRequest.getFullName())
                .password(passwordEncoder.encode(password))
                .build()
        );
    }

    @GetMapping("/{userId}/stats-against-me")
    public StatsAgainstUserDTO getStats(
            @PathVariable UUID userId,
            @AuthenticatedUser UserEntity authenticatedUser
    ) {
        final var authUserId = authenticatedUser.getId();
        if (userId.equals(authUserId)) {
            return StatsAgainstUserDTO.builder().build();
        }

        return userService.getStatsAgainstUser(authUserId, userId);
    }

    @GetMapping("/{userId}/checkPermission")
    public boolean checkPermission (
            @AuthenticatedUser UserEntity user,
            Authentication authentication,
            @PathVariable UUID userId,
            @RequestParam String targetId,
            @RequestParam String targetType,
            @RequestParam String permission
    ){
        boolean isAdmin = SecurityUtil.hasRole(user, RoleEntity.Roles.ADMIN);
        boolean isSelf = Objects.equals(userId, user.getId());
        if (!isAdmin && !isSelf) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Can not check permissions of other users");
        }
        return permissionEvaluator.hasPermission(authentication, targetId, targetType, permission);
    }
}
