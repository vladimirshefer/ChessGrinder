package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.AuthenticatedUserArgumentResolver;
import com.chessgrinder.chessgrinder.security.entitypermissionevaluator.TournamentEntityPermissionEvaluatorImpl;
import com.chessgrinder.chessgrinder.service.MatchService;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.chessgrinder.chessgrinder.entities.RoleEntity.Roles.ADMIN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MatchControllerTest {

    private MockMvc mockMvc;
    private UUID tournamentId = UUID.randomUUID();
    private UUID matchId = UUID.randomUUID();
    private UserRepository userRepository;
    private TournamentRepository tournamentRepository;
    private ParticipantRepository participantRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        tournamentRepository = mock(TournamentRepository.class);
        participantRepository = mock(ParticipantRepository.class);
        var controller = new MatchController(
                mock(MatchService.class),
                new TournamentEntityPermissionEvaluatorImpl(
                        userRepository,
                        tournamentRepository,
                        participantRepository
                )
        );
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticatedUserArgumentResolver(userRepository))
                .build();
    }

    @SneakyThrows
    @Test
    void testSetResultAsAdmin() {
        SecurityContextHolder.getContext().setAuthentication(createAuthentication("admin"));
        UserEntity userEntity = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .roles(List.of(
                        RoleEntity.builder()
                                .name(ADMIN)
                                .build()
                ))
                .build();
        doAnswer((invocation) -> userEntity).when(userRepository).findByUsername(any());
        doAnswer((invocation) -> Optional.ofNullable(userEntity)).when(userRepository).findById(any());

        doAnswer(invocation -> true).when(tournamentRepository).existsById(any());
        doAnswer(invocation -> ParticipantEntity.builder().build()).when(participantRepository).findById(any());

        mockMvc.perform(post("/tournament/" + tournamentId + "/round/1/match/" + matchId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"matchResult\": \"WHITE_WIN\"}"))
                .andExpect(status().isOk());
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

