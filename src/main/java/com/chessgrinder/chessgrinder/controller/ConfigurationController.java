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
    @Value("${chessgrinder.feature.auth.signupWithPasswordEnabled:false}")
    private boolean signupWithPasswordEnabled;
    @Value("${chessgrinder.feature.eloServiceEnabled:false}")
    private boolean eloServiceEnabled;
    @Value("${chessgrinder.feature.tournament.submitResultByParticipantsEnabled:false}")
    private boolean submitResultByParticipantsEnabled;

    @GetMapping
    public Map<String, String> getConfiguration() {
        return new HashMap<>(){{
            put("signupWithPasswordEnabled", String.valueOf(signupWithPasswordEnabled));
            put("eloServiceEnabled", String.valueOf(eloServiceEnabled));
            put("submitResultByParticipantsEnabled", String.valueOf(submitResultByParticipantsEnabled));
        }};
    }
}
