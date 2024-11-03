package com.chessgrinder.chessgrinder.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/configuration")
public class ConfigurationController {
    @Value("${chessgrinder.feature.auth.password:false}")
    private boolean authPassword;
    @Value("${chessgrinder.feature.chess.rating:false}")
    private boolean chessRating;
    @Value("${chessgrinder.feature.chess.results.submit:false}")
    private boolean chessResultsSubmit;

    @GetMapping
    public Map<String, String> getConfiguration() {
        return new HashMap<>(){{
            put("auth.password", String.valueOf(authPassword));
            put("chess.rating", String.valueOf(chessRating));
            put("chess.results.submit", String.valueOf(chessResultsSubmit));
        }};
    }
}
