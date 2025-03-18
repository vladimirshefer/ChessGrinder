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
import com.chessgrinder.chessgrinder.testutil.repository.TestJpaRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Objects;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TournamentControllerTest {

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
        tournamentController = new TournamentController(
                Mockito.mock(),
                tournamentRepository,
                userRepository,
                participantRepository,
                tournamentMapper,
                new MatchMapper(participantMapper),
                participantMapper
        );

        mockMvc = MockMvcBuilders.standaloneSetup(tournamentController).build();
    }

    @Test
    void participate_shouldParticipateSuccessfully_whenAllConditionsMet() throws Exception {
        String username = "user@user.user";
        String nickname = "testNickname";

        UserEntity userEntity = userRepository.save(new UserEntity());
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
                .andExpect(status().isOk())
                .andExpect(content().string(username));

        // Verify save is called
        verify(participantRepository).save(any(ParticipantEntity.class));
    }

    @Test
    void participate_shouldThrowException_whenUserNotAuthenticated() throws Exception {
        UUID tournamentId = UUID.randomUUID();

        // Perform the request
        mockMvc.perform(post("/tournament/{tournamentId}/action/participate", tournamentId)
                        .principal(mockAuth("user@user.user", false)))
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

        mockMvc.perform(post("/tournament/{tournamentId}/action/participate", tournament.getId())
                        .principal(authentication))
                .andExpect(status().isBadRequest());
    }

    @NotNull
    private static Authentication mockAuth(String username, boolean isAuthenticated) {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(isAuthenticated);
        when(authentication.getName()).thenReturn(username);
        return authentication;
    }
}
