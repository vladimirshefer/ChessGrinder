package com.chessgrinder.chessgrinder.controller;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.service.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/page/main")
@RequiredArgsConstructor
public class MainPageController {

    @Autowired
    private final UserService userService;

    @GetMapping
    public List<UserDto> getUserDto() {

        return userService.getInfoForMainPage();
    }
}
