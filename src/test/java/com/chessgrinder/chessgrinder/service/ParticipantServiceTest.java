package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.mappers.ParticipantMapper;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.entitypermissionevaluator.EntityPermissionEvaluator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ParticipantServiceTest {

    private ParticipantRepository participantRepository;
    private UserRepository userRepository;
    private TournamentRepository tournamentRepository;
    private ParticipantService participantService;
    private UUID tournamentId;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        participantRepository = Mockito.mock(ParticipantRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        tournamentRepository = Mockito.mock(TournamentRepository.class);
        participantService = new ParticipantService(
                participantRepository,
                tournamentRepository,
                Mockito.mock(ParticipantMapper.class),
                userRepository,
                Mockito.mock(EntityPermissionEvaluator.class)
        );
        tournamentId = UUID.randomUUID();
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(
                TournamentEntity.builder().id(tournamentId).status(TournamentStatus.PLANNED).build()
        ));
    }

    @Test
    void addParticipantToTheTournament_shouldMakeModerator_whenUserHasModeratorRole() {
        assertThat(addUserWithRoles(RoleEntity.Roles.MODERATOR).isModerator()).isTrue();
    }

    @Test
    void addParticipantToTheTournament_shouldMakeModerator_whenUserIsAdmin() {
        assertThat(addUserWithRoles(RoleEntity.Roles.ADMIN).isModerator()).isTrue();
    }

    @Test
    void addParticipantToTheTournament_shouldNotMakeModerator_whenUserIsPlain() {
        assertThat(addUserWithRoles(RoleEntity.Roles.USER).isModerator()).isFalse();
    }

    @Test
    void addParticipantToTheTournament_shouldNotMakeModerator_whenNoUser() {
        participantService.addParticipantToTheTournament(tournamentId, ParticipantDto.builder().name("guest").build());

        assertThat(savedParticipant().isModerator()).isFalse();
    }

    private ParticipantEntity addUserWithRoles(String role) {
        UUID userId = UUID.randomUUID();
        UserEntity user = UserEntity.builder()
                .id(userId)
                .roles(List.of(RoleEntity.builder().name(role).build()))
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        participantService.addParticipantToTheTournament(
                tournamentId,
                ParticipantDto.builder().name("nick").userId(userId.toString()).build()
        );

        return savedParticipant();
    }

    private ParticipantEntity savedParticipant() {
        ArgumentCaptor<ParticipantEntity> saved = ArgumentCaptor.forClass(ParticipantEntity.class);
        verify(participantRepository).save(saved.capture());
        return saved.getValue();
    }
}
