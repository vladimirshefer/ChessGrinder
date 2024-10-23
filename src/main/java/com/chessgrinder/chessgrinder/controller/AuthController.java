package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.captcha.CaptchaRequired;
import com.chessgrinder.chessgrinder.security.principal.MyUserDetails;
import com.chessgrinder.chessgrinder.service.EmailService;
import com.chessgrinder.chessgrinder.util.CacheUtil;
import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String USERNAME_REGEX = "^[a-zA-Z][a-zA-Z0-9]+$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${chessgrinder.feature.auth.password:false}")
    private boolean isSignupWithPasswordEnabled;

    @Value("${chessgrinder.server.http.url}")
    private String serverUrl;

    /**
     * One time login token -> email
     */
    @VisibleForTesting
    Map<String, String> ott2email = CacheUtil.createCache(100);
    /**
     * One time login token -> time of creation request
     */
    @VisibleForTesting
    Map<String, LocalDateTime> ott2date = CacheUtil.createCache(100);

    /**
     * Create an instant auth link. User will receive the link via email.
     */
    @GetMapping("/instant/{authId}")
    public void instant(
            @PathVariable("authId") String authId
    ) {
        if (!ott2email.containsKey(authId)) {
            throw new ResponseStatusException(NOT_FOUND, "Auth ID not found");
        }
        String email = ott2email.get(authId);
        UserEntity user = userRepository.findByUsername(email);
        if (user == null) {
            log.error("Unknown email {}", email);
            return; // Do not throw exception to avoid emails bruteforce.
        }
        if (Objects.requireNonNull(ott2date.get(authId)).plusMinutes(15).isBefore(LocalDateTime.now())) {
            ott2email.remove(authId);
            ott2date.remove(authId);
            throw new ResponseStatusException(NOT_FOUND, "Auth ID expired");
        }
        var principal = new MyUserDetails(user);
        var authReq = new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authReq);
        ott2email.remove(authId);
        ott2date.remove(authId);
    }

    /**
     * Handler for instant auth link. It authenticates user.
     */
    @CaptchaRequired
    @GetMapping("/instant/init")
    public void instantInit(
            @RequestParam("email") String email
    ) {
        if (!email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("Invalid email: " + email);
        }
        UserEntity user = userRepository.findByUsername(email);
        if (user == null) {
            log.error("Unknown email {}", email);
            return; // Do not throw exception to avoid emails bruteforce.
        }
        String ott = UUID.randomUUID().toString();
        ott2email.put(ott, email);
        ott2date.put(ott, LocalDateTime.now());
        emailService.sendSimpleMessage(email, "Login link", serverUrl + "/auth/instant/" + ott);
    }

}
