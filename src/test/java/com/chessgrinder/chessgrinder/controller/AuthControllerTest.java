package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.principal.AuthorizedUserEntityProvider;
import com.chessgrinder.chessgrinder.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthController authController;

    private static final String VALID_EMAIL = "test@example.com";
    private static final String INVALID_EMAIL = "invalid-email";
    private UserEntity user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new UserEntity();
        user.setUsername(VALID_EMAIL);
        user.setRoles(List.of());
        authController.setIsInstantLoginEnabled(true);
    }

    @Test
    void test_valid_email_and_correct_token() {
        when(userRepository.findByUsername(VALID_EMAIL)).thenReturn(user);

        authController.instantInit(VALID_EMAIL);

        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        verify(emailService, times(1)).sendSimpleMessage(
                eq(VALID_EMAIL), anyString(), urlCaptor.capture()
        );

        String capturedUrl = urlCaptor.getValue();
        assertTrue(capturedUrl.contains("/auth/instant/"), "The URL should contain '/auth/instant/'");

        String authId = capturedUrl.substring(capturedUrl.lastIndexOf("/") + 1);

        authController.instant(authId);

        AuthorizedUserEntityProvider principal = (AuthorizedUserEntityProvider) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assertEquals(VALID_EMAIL, principal.getUserEntity().getUsername());
    }

    @Test
    void instantInit_invalidEmail() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authController.instantInit(INVALID_EMAIL);
        });

        assertEquals("Invalid email: " + INVALID_EMAIL, exception.getMessage());
    }

    @Test
    void instantInit_nonExistingUser() {
        when(userRepository.findByUsername(VALID_EMAIL)).thenReturn(null);

        authController.instantInit(VALID_EMAIL);

        verify(emailService, never()).sendSimpleMessage(anyString(), anyString(), anyString());
    }

    @Test
    void instant_nonExistingAuthId() {
        String authId = "nonExistingAuthId";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authController.instant(authId);
        });

        assertEquals("404 NOT_FOUND \"Auth ID not found\"", exception.getMessage());
    }

    @Test
    void instant_expiredAuthId_throwsResponseStatusException() {
        when(userRepository.findByUsername(VALID_EMAIL)).thenReturn(user);
        String authId = "expiredAuthId";
        authController.ott2email.put(authId, VALID_EMAIL);
        authController.ott2date.put(authId, LocalDateTime.now().minusMinutes(20));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authController.instant(authId);
        });

        assertEquals("404 NOT_FOUND \"Auth ID expired\"", exception.getMessage());
    }

}
