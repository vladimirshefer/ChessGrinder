package com.chessgrinder.chessgrinder.security.entitypermissionevaluator;

import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.testutil.repository.TestJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

class TournamentEntityPermissionEvaluatorImplTest {

    private TournamentEntityPermissionEvaluatorImpl evaluator;
    private UserRepository userRepository;
    private TournamentRepository tournamentRepository;
    private ParticipantRepository participantRepository;

    @BeforeEach
    void setUp() {
        userRepository = TestJpaRepository.of(UserRepository.class);
        tournamentRepository = TestJpaRepository.of(TournamentRepository.class);
        participantRepository = Mockito.spy(TestJpaRepository.of(ParticipantRepository.class));
        Mockito.doAnswer(invocation -> {
            Map<UUID, ParticipantEntity> data = TestJpaRepository.getData((ParticipantRepository) invocation.getMock());
            return data.values().stream()
                    .filter(it -> it.getTournament() != null && it.getTournament().getId().equals(invocation.getArgument(0)))
                    .filter(it -> it.getUser() != null && it.getUser().getId().equals(invocation.getArgument(1)))
                    .findFirst()
                    .orElse(null);
        }).when(participantRepository).findByTournamentIdAndUserId(any(), any());
        evaluator = new TournamentEntityPermissionEvaluatorImpl(userRepository, tournamentRepository, participantRepository);
    }

    @Test
    void hasPermission_shouldReturnFalse_forInvalidInput() {
        assertFalse(evaluator.hasPermission((UUID) null, UUID.randomUUID().toString(), TournamentEntityPermissionEvaluatorImpl.Permissions.MODERATOR.name()));
        assertFalse(evaluator.hasPermission(UUID.randomUUID(), "", TournamentEntityPermissionEvaluatorImpl.Permissions.MODERATOR.name()));
        assertFalse(evaluator.hasPermission(UUID.randomUUID(), "not-a-uuid", TournamentEntityPermissionEvaluatorImpl.Permissions.MODERATOR.name()));
        assertFalse(evaluator.hasPermission(UUID.randomUUID(), UUID.randomUUID().toString(), ""));
    }

    @Test
    void hasPermission_shouldReturnTrue_forAdmin() {
        UserEntity admin = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .roles(List.of(RoleEntity.builder().name(RoleEntity.Roles.ADMIN).build()))
                .build());
        TournamentEntity tournament = tournamentRepository.save(TournamentEntity.builder()
                .id(UUID.randomUUID())
                .status(TournamentStatus.PLANNED)
                .build());

        assertTrue(evaluator.hasPermission(admin.getId(), tournament.getId().toString(), TournamentEntityPermissionEvaluatorImpl.Permissions.MODERATOR.name()));
        assertTrue(evaluator.hasPermission(admin.getId(), tournament.getId().toString(), TournamentEntityPermissionEvaluatorImpl.Permissions.OWNER.name()));
    }

    @Test
    void hasPermission_shouldReturnTrue_forOwnerAsOwnerAndModerator() {
        UserEntity owner = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .roles(List.of())
                .build());
        TournamentEntity tournament = tournamentRepository.save(TournamentEntity.builder()
                .id(UUID.randomUUID())
                .owner(owner)
                .status(TournamentStatus.PLANNED)
                .build());

        assertTrue(evaluator.hasPermission(owner.getId(), tournament.getId().toString(), TournamentEntityPermissionEvaluatorImpl.Permissions.OWNER.name()));
        assertTrue(evaluator.hasPermission(owner.getId(), tournament.getId().toString(), TournamentEntityPermissionEvaluatorImpl.Permissions.MODERATOR.name()));
    }

    @Test
    void hasPermission_shouldReturnTrue_forModeratorParticipantOnlyAsModerator() {
        UserEntity user = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .roles(List.of())
                .build());
        TournamentEntity tournament = tournamentRepository.save(TournamentEntity.builder()
                .id(UUID.randomUUID())
                .status(TournamentStatus.PLANNED)
                .build());
        participantRepository.save(ParticipantEntity.builder()
                .id(UUID.randomUUID())
                .user(user)
                .tournament(tournament)
                .isModerator(true)
                .build());

        assertTrue(evaluator.hasPermission(user.getId(), tournament.getId().toString(), TournamentEntityPermissionEvaluatorImpl.Permissions.MODERATOR.name()));
        assertFalse(evaluator.hasPermission(user.getId(), tournament.getId().toString(), TournamentEntityPermissionEvaluatorImpl.Permissions.OWNER.name()));
    }
}
