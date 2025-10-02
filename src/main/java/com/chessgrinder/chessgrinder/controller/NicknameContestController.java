package com.chessgrinder.chessgrinder.controller;

import java.util.NoSuchElementException;
import java.util.UUID;

import com.chessgrinder.chessgrinder.service.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/nickname-contest")
@RequiredArgsConstructor
public class NicknameContestController {

    private final StrawpollService strawpollService;
    private static final String STRAWPOLL_URL_PREFIX = "https://strawpoll.com/";

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @PostMapping(value = "/{tournamentId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String create(@PathVariable UUID tournamentId) {
        try {
            String pollId = strawpollService.getOrCreateContestPollId(tournamentId);
            return STRAWPOLL_URL_PREFIX + pollId;
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @GetMapping(value = "/{tournamentId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String get(@PathVariable UUID tournamentId) {
        String pollId = strawpollService.getPollId(tournamentId);
        if (pollId != null) {
            return STRAWPOLL_URL_PREFIX + pollId;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No strawpoll found for tournament id " + tournamentId, null) ;
    }

}
