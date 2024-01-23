package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.ListDto;
import com.chessgrinder.chessgrinder.dto.UserDto;
import com.chessgrinder.chessgrinder.dto.UserHistoryRecordDto;
import com.chessgrinder.chessgrinder.dto.UserReputationHistoryRecordDto;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.exceptions.UserNotFoundException;
import com.chessgrinder.chessgrinder.mappers.ParticipantMapper;
import com.chessgrinder.chessgrinder.mappers.TournamentMapper;
import com.chessgrinder.chessgrinder.repositories.*;
import com.chessgrinder.chessgrinder.security.CustomOAuth2User;
import com.chessgrinder.chessgrinder.service.UserService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

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

    @GetMapping
    public ListDto<UserDto> getUsers() {
        return ListDto.<UserDto>builder().values(userService.getAllUsers()).build();
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

    @GetMapping("/me")
    public UserDto me(
            Authentication authentication
    ) {
        if (authentication == null) {
            throw new ResponseStatusException(401, "Not logged in", null);
        }
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        String email = principal.getEmail();
        if (email == null) {
            return null;
        }
        return userService.getUserByUserName(email);
    }

    @GetMapping("/{userIdOrUsername}/history")
    public ListDto<UserHistoryRecordDto> history(
            @PathVariable String userIdOrUsername
    ) {
        UserEntity user = findUserByIdOrUsername(userIdOrUsername);
        List<ParticipantEntity> participants = participantRepository.findAllByUserId(user.getId());
        List<UserHistoryRecordDto> history = participants.stream()
                .map(participant ->
                        UserHistoryRecordDto.builder()
                                .tournament(tournamentMapper.toDto(participant.getTournament()))
                                .participant(participantMapper.toDto(participant))
                                .place(-1) // TODO
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
            @RequestBody UserDto jsonObject,
            Authentication authentication
    ) {
        if (authentication == null)
            throw new ResponseStatusException(401, "Not logged in", null);
        final CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        final UserEntity authUser = principal.getUser();
        if (authUser == null) return;

        UserEntity userEntity = userRepository.findByUsername(userName);
        if (userEntity == null) {
            throw new UsernameNotFoundException("No user with username " + userName);
        }
        if (!authUser.getId().equals(userEntity.getId())) {
            throw new ResponseStatusException(403, "Not allowed to change other's name", null);
        }

        final String newUserFullName = jsonObject.getName();
        userEntity.setName(newUserFullName);
        userRepository.save(userEntity);
    }
}
