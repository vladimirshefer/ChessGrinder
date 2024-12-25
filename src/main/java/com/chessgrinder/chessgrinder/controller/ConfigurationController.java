package com.chessgrinder.chessgrinder.controller;

import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/configuration")
public class ConfigurationController {
    @Value("${chessgrinder.feature.auth.password:false}")
    private boolean authPassword;
    @Value("${chessgrinder.feature.auth.instant:false}")
    private boolean authInstant;
    @Value("${chessgrinder.feature.chess.rating:false}")
    private boolean chessRating;
    @Value("${chessgrinder.feature.chess.results.submit:false}")
    private boolean chessResultsSubmit;
    @Value("${chessgrinder.captcha.site:}")
    private String captchaPublicKey;
    @Autowired(required = false)
    @Nullable
    private BuildProperties buildProperties;

    @GetMapping
    public Map<String, String> getConfiguration() {
        return new HashMap<>() {{
            put("auth.password", String.valueOf(authPassword));
            put("auth.instant", String.valueOf(authInstant));
            put("chess.rating", String.valueOf(chessRating));
            put("chess.results.submit", String.valueOf(chessResultsSubmit));
            put("captcha.site", captchaPublicKey);
            put("build.time", (buildProperties!=null && buildProperties.getTime() != null)
                    ? DateTimeFormatter.ofPattern("yyyy.MM.dd")
                    .format(LocalDateTime.ofInstant(buildProperties.getTime(), ZoneOffset.UTC))
                    : null
            );
        }};
    }
}
