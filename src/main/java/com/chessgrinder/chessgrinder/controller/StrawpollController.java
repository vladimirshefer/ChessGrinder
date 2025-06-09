package com.chessgrinder.chessgrinder.controller;

import java.util.*;

import com.chessgrinder.chessgrinder.service.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/strawpoll")
@RequiredArgsConstructor
public class StrawpollController {

    private final StrawpollService strawpollService;

    @GetMapping(value = "/{tournamentId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String makeStrawpoll(@PathVariable UUID tournamentId) {
        String result = strawpollService.createStrawpoll(tournamentId);
        return result;
    }
}
