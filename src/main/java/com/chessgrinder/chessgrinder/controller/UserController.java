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

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
    private static final String START_DATE_STRING = "01.01.1970";
    private static final String END_DATE_STRING = "01.01.2100";
    private static final String DATE_FORMAT_STRING = "dd.MM.yyyy";


    @GetMapping
    public ListDto<UserDto> getUsers() {
        final List<UserDto> allUsers = userService.getAllUsers().stream()
                .sorted(Comparator.comparing(UserDto::getTotalPoints).reversed())
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

    @GetMapping("/{userIdOrUsername}/totalPoints")
    public BigDecimal getTotalPoints(
            @PathVariable String userIdOrUsername,
            @RequestParam(required = false, name = "startSeasonDate") String startSeasonDateString,
            @RequestParam(required = false, name = "endSeasonDate") String endSeasonDateString) {
        Date startSeasonDate;
        Date endSeasonDate;

//        LocalDateTime localDateTime = LocalDate.parse((startSeasonDateString == null ? START_DATE_STRING : startSeasonDateString), DateTimeFormatter.ofPattern(DATE_FORMAT_STRING)).atStartOfDay();
//        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

//        final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STRING);
        try {
//            DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_FORMAT_STRING);
//            LocalDate date = LocalDate.parse(startSeasonDateString == null ? START_DATE_STRING : startSeasonDateString, format);
            startSeasonDate = getDateFromString(startSeasonDateString, START_DATE_STRING);
            endSeasonDate = getDateFromString(endSeasonDateString, END_DATE_STRING);
//            startSeasonDate = DATE_FORMAT.parse(startSeasonDateString == null ? START_DATE_STRING : startSeasonDateString);
//            endSeasonDate = DATE_FORMAT.parse(endSeasonDateString == null ? END_DATE_STRING : endSeasonDateString);
        } catch (Exception e) {
            throw new ResponseStatusException(400, "Can't parse start or end season date with format " + DATE_FORMAT_STRING, e);
        }
        final var startSeasonDateFinal = startSeasonDate;
        final var endSeasonDateFinal = endSeasonDate;

        UserEntity user = findUserByIdOrUsername(userIdOrUsername);
        //TODO упомянуть При создании турнира иниц. локальное время!!
        List<ParticipantEntity> participants = participantRepository.findAllByUserId(user.getId());
        return participants.stream()
                .filter(participant -> {
                    final LocalDateTime tournamentDateTime = participant.getTournament().getDate();
                    Date tournamentDate = Date.from(tournamentDateTime.toInstant(ZoneOffset.UTC));
                    return !tournamentDate.before(startSeasonDateFinal) && !tournamentDate.after(endSeasonDateFinal);
                })
                .map(ParticipantEntity::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static Date getDateFromString(String dateString, String defaultDateString) {
        String formalDate = (dateString == null ? defaultDateString : dateString);
        LocalDateTime localDateTime = LocalDate.parse(formalDate, DateTimeFormatter.ofPattern(DATE_FORMAT_STRING)).atStartOfDay();
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
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
}
