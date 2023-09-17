package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.MemberDto;
import com.chessgrinder.chessgrinder.exceptions.UserNotFoundException;
import com.chessgrinder.chessgrinder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "localhost:3000")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<MemberDto> get() {
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public MemberDto addParticipantToTournament(@PathVariable String userId) throws UserNotFoundException {
        try {
            MemberDto user = userService.getUserByUserId(userId);
            if (user != null) {
                return user;
            }
        } catch (Exception e){

        }
        return userService.getUserByUserName(userId);
    }

}
