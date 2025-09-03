package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.mappers.MatchMapper;
import com.chessgrinder.chessgrinder.mappers.ParticipantMapper;
import com.chessgrinder.chessgrinder.mappers.TournamentMapper;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.service.TournamentService;
import com.chessgrinder.chessgrinder.dto.TournamentDto;
import com.chessgrinder.chessgrinder.testutil.repository.TestJpaRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.chessgrinder.chessgrinder.security.AuthenticatedUserArgumentResolver;

import java.util.Objects;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TournamentControllerTest {

    private TournamentService tournamentService;

    private TournamentController tournamentController;

    private UserRepository userRepository;

    private TournamentRepository tournamentRepository;

    private ParticipantRepository participantRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        participantRepository = Mockito.spy(TestJpaRepository.of(ParticipantRepository.class));
        tournamentRepository = Mockito.spy(TestJpaRepository.of(TournamentRepository.class));
        userRepository = Mockito.mock(UserRepository.class);
        Mockito.doAnswer(
                        i -> TestJpaRepository.getData(participantRepository).values().stream()
                                .filter(it -> Objects.equals(it.getTournament().getId(), i.getArgument(0)))
                                .count()
                )
                .when(participantRepository)
                .countByTournament(any());

        TournamentMapper tournamentMapper = new TournamentMapper();
        ParticipantMapper participantMapper = new ParticipantMapper(tournamentMapper);
        tournamentService = Mockito.mock(TournamentService.class);
        tournamentController = new TournamentController(
                tournamentService,
                tournamentRepository,
                participantRepository,
                tournamentMapper,
                new MatchMapper(participantMapper),
                participantMapper
        );

        mockMvc = MockMvcBuilders.standaloneSetup(tournamentController)
                .setCustomArgumentResolvers(new AuthenticatedUserArgumentResolver(userRepository))
                .build();
    }

    @Test
    void createTournament_shouldCreateAndReturnTournamentWithOwner() throws Exception {
        String username = "admin@user.user";

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(userEntity);

        TournamentDto dto = TournamentDto.builder()
                .id(UUID.randomUUID().toString())
                .status(TournamentStatus.PLANNED)
                .roundsNumber(6)
                .build();
        when(tournamentService.createTournament(any(), eq(userEntity))).thenReturn(dto);

        mockMvc.perform(post("/tournament")
                        .principal(mockAuth(username, true)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":\"" + dto.getId() + "\"}", false));

        verify(tournamentService).createTournament(any(), eq(userEntity));
    }

    @Test
    void participate_shouldParticipateSuccessfully_whenAllConditionsMet() throws Exception {
        String username = "user@user.user";
        String nickname = "testNickname";

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(userEntity);

        TournamentEntity tournament = tournamentRepository.save(
                TournamentEntity.builder()
                        .status(TournamentStatus.PLANNED)
                        .build()
        );

        // Perform the request
        mockMvc.perform(post("/tournament/{tournamentId}/action/participate", tournament.getId())
                        .param("nickname", nickname)
                        .principal(mockAuth(username, true)))
                .andExpect(status().isOk());

        // Verify save is called
        verify(participantRepository).save(any(ParticipantEntity.class));
    }

    @Test
    void participate_shouldThrowException_whenUserNotAuthenticated() throws Exception {
        UUID tournamentId = UUID.randomUUID();

        String username = "user@user.user";
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(userEntity);

        // Perform the request
        mockMvc.perform(post("/tournament/{tournamentId}/action/participate", tournamentId)
                        .principal(mockAuth(username, false)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void participate_shouldThrowException_whenTournamentAlreadyStarted() throws Exception {
        String username = "user@user.user";

        TournamentEntity tournament = tournamentRepository.save(
                TournamentEntity.builder()
                        .status(TournamentStatus.ACTIVE)
                        .build()
        );

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(userEntity);

        mockMvc.perform(post("/tournament/{tournamentId}/action/participate", tournament.getId())
                        .principal(mockAuth(username, true)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void participate_shouldThrowException_whenParticipantLimitReached() throws Exception {
        String username = "user@user.user";

        Authentication authentication = mockAuth(username, true);

        TournamentEntity tournament = tournamentRepository.save(
                TournamentEntity.builder()
                        .status(TournamentStatus.PLANNED)
                        .registrationLimit(10)
                        .build()
        );

        for (int i = 0; i < 10; i++) {
            participantRepository.save(ParticipantEntity.builder().id(UUID.randomUUID()).tournament(tournament).build());
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(userEntity);

        mockMvc.perform(post("/tournament/{tournamentId}/action/participate", tournament.getId())
                        .principal(authentication))
                .andExpect(status().isBadRequest());
    }

    @NotNull
    private static Authentication mockAuth(String username, boolean isAuthenticated) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(isAuthenticated);
        when(authentication.getName()).thenReturn(username);
        when(authentication.getPrincipal()).thenReturn(username);
//         Populate SecurityContext so that AuthenticatedUserArgumentResolver can read it
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
}
