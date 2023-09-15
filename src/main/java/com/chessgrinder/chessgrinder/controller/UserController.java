package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.MemberDto;
import com.chessgrinder.chessgrinder.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<MemberDto> get() {
        return userService.getAllUsers();
    }

}
