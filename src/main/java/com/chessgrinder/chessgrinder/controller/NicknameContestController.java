package com.chessgrinder.chessgrinder.controller;

import java.util.*;

import com.chessgrinder.chessgrinder.service.*;
import com.chessgrinder.chessgrinder.util.CacheUtil;
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

    public static final Map<UUID, String> cachedLinks = CacheUtil.createCache(20);

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @PostMapping(value = "/{tournamentId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String create(@PathVariable UUID tournamentId) {
        synchronized (cachedLinks) {
            if (cachedLinks.containsKey(tournamentId)) {
                return cachedLinks.get(tournamentId);
            }
            String result = strawpollService.createStrawpoll(tournamentId);
            cachedLinks.put(tournamentId, result);
            return result;
        }
    }

    @GetMapping(value = "/{tournamentId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String get(@PathVariable UUID tournamentId) {
        if (cachedLinks.containsKey(tournamentId)) {
            return cachedLinks.get(tournamentId);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No strawpoll found for tournament id " + tournamentId, null) ;
    }

}
