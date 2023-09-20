package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.MemberDto;
import com.chessgrinder.chessgrinder.exceptions.UserNotFoundException;
import com.chessgrinder.chessgrinder.security.CustomOAuth2User;
import com.chessgrinder.chessgrinder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Object getUsers() {
        return Map.of("values", userService.getAllUsers());
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

    @GetMapping("/me")
    public MemberDto me(
            Authentication authentication
    ){
        if (authentication == null) {
            return null;
        }
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        String email = principal.getEmail();
        if (email == null) {
            return null;
        }
        return userService.getUserByUserName(email);
    }

}
