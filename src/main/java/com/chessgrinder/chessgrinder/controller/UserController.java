package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.exceptions.*;
import com.chessgrinder.chessgrinder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<MemberDto> get() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public MemberDto addParticipantToTournament(@PathVariable UUID userId) throws UserNotFoundException {
        return userService.getUserById(userId);
    }

    @GetMapping("/{userName}")
    public MemberDto addParticipantToTournament(@PathVariable String userName) throws UserNotFoundException  {
        return userService.getUserByUserName(userName);
    }


}
