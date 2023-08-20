package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.pages.*;
import com.chessgrinder.chessgrinder.service.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pages/main")
@CrossOrigin(origins = "localhost:3000")
@RequiredArgsConstructor
public class MainPageController {
    private final MainPageService mainPageService;

    @GetMapping
    public MainPageDto getDto() {
        return mainPageService.getInfoForMainPage();
    }
}
