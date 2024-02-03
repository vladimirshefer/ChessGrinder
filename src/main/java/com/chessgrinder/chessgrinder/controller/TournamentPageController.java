package com.chessgrinder.chessgrinder.controller;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.pages.*;
import com.chessgrinder.chessgrinder.service.*;
import lombok.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pages/tournament")
@RequiredArgsConstructor
public class TournamentPageController {
    private final TournamentPageService tournamentPageService;

    //TODO изменить сортировку на странице турнира по месту в турнире (думаю, там тоже будет нужен этот алгоритм)
    @GetMapping("{tournamentId}")
    public TournamentPageDto getDto(@PathVariable UUID tournamentId) {
        return tournamentPageService.getTournamentData(tournamentId);
    }
}
