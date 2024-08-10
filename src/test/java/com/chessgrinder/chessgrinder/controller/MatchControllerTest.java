package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.SubmitMatchResultRequestDto;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.AuthenticatedUserArgumentResolver;
import com.chessgrinder.chessgrinder.service.MatchService;
import com.chessgrinder.chessgrinder.testutil.repository.TestJpaRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MatchControllerTest {

    private MockMvc mockMvc;
    private UUID tournamentId = UUID.randomUUID();
    private UUID matchId = UUID.randomUUID();
    private UserRepository userRepository;
    private TournamentRepository tournamentRepository;
    private ParticipantRepository participantRepository;
    private MatchService matchService;

    @BeforeEach
    void setUp() {
        userRepository = spy(TestJpaRepository.of(UserRepository.class));
        doAnswer(invocation -> TestJpaRepository
                .getData(userRepository)
                .values()
                .stream()
                .filter(it -> Objects.equals(it.getUsername(), invocation.getArgument(0)))
                .findFirst().orElse(null)
        ).when(userRepository).findByUsername(any());
        matchService = mock(MatchService.class);
        var controller = new MatchController(matchService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticatedUserArgumentResolver(userRepository))
                .build();
    }

    @SneakyThrows
    @Test
    void testSetResult() {
        SecurityContextHolder.getContext().setAuthentication(createAuthentication("admin"));
        UserEntity user = userRepository.save(UserEntity.builder().id(UUID.randomUUID()).username("admin").build());

        mockMvc.perform(post("/tournament/" + tournamentId + "/round/1/match/" + matchId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"matchResult\": \"WHITE_WIN\"}"))
                .andExpect(status().isOk());

        SubmitMatchResultRequestDto expectedResultDto = SubmitMatchResultRequestDto.builder().matchResult(MatchResult.WHITE_WIN).build();
        verify(matchService).submitMatchResult(user, matchId, expectedResultDto);
    }

    private static Authentication createAuthentication(String username) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of(new SimpleGrantedAuthority("dummy"))
        );
        return authentication;
    }
}

