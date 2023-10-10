package com.chessgrinder.chessgrinder.controller;

import java.util.*;

import com.chessgrinder.chessgrinder.chessengine.*;
import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.exceptions.UserNotFoundException;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.CustomOAuth2User;
import com.chessgrinder.chessgrinder.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    private final SwissMatchupStrategyImpl swissEngine;

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
    //TODO test

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

    @PostMapping("/test")
    public List<MatchDto> test(@RequestBody List<ParticipantDto> users) {
        return swissEngine.matchUp(users, null);
    }

}
