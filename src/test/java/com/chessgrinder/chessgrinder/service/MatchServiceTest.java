package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.SubmitMatchResultRequestDto;
import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.RoundEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.repositories.MatchRepository;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.entitypermissionevaluator.TournamentEntityPermissionEvaluatorImpl;
import com.chessgrinder.chessgrinder.testutil.repository.TestJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

class MatchServiceTest {

    private MatchService matchService;
    private MatchRepository matchRepository;
    private UserRepository userRepository;
    private TournamentRepository tournamentRepository;
    private ParticipantRepository participantRepository;

    @BeforeEach
    void setUp() {
        matchRepository = TestJpaRepository.of(MatchRepository.class);
        userRepository = TestJpaRepository.of(UserRepository.class);
        tournamentRepository = TestJpaRepository.of(TournamentRepository.class);
        participantRepository = Mockito.spy(TestJpaRepository.of(ParticipantRepository.class));
        Mockito.doAnswer(invocation -> {
            Map<UUID, ParticipantEntity> data = TestJpaRepository.getData((ParticipantRepository) invocation.getMock());
            ParticipantEntity participantEntity = data.values().stream()
                    .filter(it -> it.getTournament() != null && it.getTournament().getId().equals(invocation.getArgument(0)))
                    .filter(it -> it.getUser() != null && it.getUser().getId().equals(invocation.getArgument(1)))
                    .findFirst()
                    .orElse(null);
            return participantEntity;
        }).when(participantRepository).findByTournamentIdAndUserId(any(), any());
        matchService = new MatchService(
                matchRepository,
                new TournamentEntityPermissionEvaluatorImpl(
                        userRepository,
                        tournamentRepository,
                        participantRepository
                )
        );
    }

    @Test
    void testAsAdmin() {
        UUID matchId = UUID.randomUUID();
        UserEntity userEntity = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .roles(
                        List.of(
                                RoleEntity.builder()
                                        .name(RoleEntity.Roles.ADMIN)
                                        .build()
                        )
                )
                .build());
        TournamentEntity tournamentEntity = cerateDummyTournament();
        MatchEntity matchEntity = matchRepository.save(MatchEntity.builder()
                .id(matchId)
                .round(RoundEntity.builder()
                        .id(UUID.randomUUID())
                        .tournament(tournamentEntity)
                        .build())
                .build()
        );
        matchService.submitMatchResult(
                userEntity,
                matchId,
                SubmitMatchResultRequestDto.builder()
                        .matchResult(MatchResult.WHITE_WIN)
                        .build()
        );
    }

    @Test
    void testAsWhite() {
        UUID matchId = UUID.randomUUID();
        UserEntity userEntity = createDummyUser();
        TournamentEntity tournamentEntity = cerateDummyTournament();
        ParticipantEntity participantEntity = createParticipant(userEntity, tournamentEntity);
        RoundEntity roundEntity = createRound(tournamentEntity);
        MatchEntity matchEntity = matchRepository.save(MatchEntity.builder()
                .id(matchId)
                .round(roundEntity)
                .participant1(participantEntity)
                .build()
        );
        matchService.submitMatchResult(
                userEntity,
                matchId,
                SubmitMatchResultRequestDto.builder()
                        .matchResult(MatchResult.WHITE_WIN)
                        .build()
        );
        MatchEntity matchAfter = matchRepository.findById(matchId).get();
        Assertions.assertEquals(MatchResult.WHITE_WIN, matchAfter.getResultSubmittedByParticipant1());
        Assertions.assertNull(matchAfter.getResultSubmittedByParticipant2());
        Assertions.assertNull(matchAfter.getResult());
    }

    @Test
    void testAsWhiteAfterBlack() {
        UUID matchId = UUID.randomUUID();
        UserEntity userEntity = createDummyUser();
        TournamentEntity tournamentEntity = cerateDummyTournament();
        ParticipantEntity participantEntity = createParticipant(userEntity, tournamentEntity);
        RoundEntity roundEntity = createRound(tournamentEntity);
        MatchEntity matchEntity = matchRepository.save(MatchEntity.builder()
                .id(matchId)
                .round(roundEntity)
                .participant1(participantEntity)
                .resultSubmittedByParticipant2(MatchResult.WHITE_WIN)
                .build()
        );
        matchService.submitMatchResult(
                userEntity,
                matchId,
                SubmitMatchResultRequestDto.builder()
                        .matchResult(MatchResult.WHITE_WIN)
                        .build()
        );
        MatchEntity matchAfter = matchRepository.findById(matchId).get();
        Assertions.assertEquals(MatchResult.WHITE_WIN, matchAfter.getResultSubmittedByParticipant1());
        Assertions.assertEquals(MatchResult.WHITE_WIN, matchAfter.getResultSubmittedByParticipant2());
        Assertions.assertEquals(MatchResult.WHITE_WIN, matchAfter.getResult());
    }

    @Test
    void testAsWhiteAfterBlackConflict() {
        UUID matchId = UUID.randomUUID();
        UserEntity userEntity = createDummyUser();
        TournamentEntity tournamentEntity = cerateDummyTournament();
        ParticipantEntity participantEntity = createParticipant(userEntity, tournamentEntity);
        RoundEntity roundEntity = createRound(tournamentEntity);
        MatchEntity matchEntity = matchRepository.save(MatchEntity.builder()
                .id(matchId)
                .round(roundEntity)
                .participant1(participantEntity)
                .resultSubmittedByParticipant2(MatchResult.WHITE_WIN)
                .build()
        );
        matchService.submitMatchResult(
                userEntity,
                matchId,
                SubmitMatchResultRequestDto.builder()
                        .matchResult(MatchResult.BLACK_WIN)
                        .build()
        );
        MatchEntity matchAfter = matchRepository.findById(matchId).get();
        Assertions.assertEquals(MatchResult.BLACK_WIN, matchAfter.getResultSubmittedByParticipant1());
        Assertions.assertEquals(MatchResult.WHITE_WIN, matchAfter.getResultSubmittedByParticipant2());
        Assertions.assertNull(matchAfter.getResult());
    }

    @Test
    void testAsBlack() {
        UUID matchId = UUID.randomUUID();
        UserEntity userEntity = createDummyUser();
        TournamentEntity tournamentEntity = cerateDummyTournament();
        ParticipantEntity participantEntity = createParticipant(userEntity, tournamentEntity);
        RoundEntity roundEntity = createRound(tournamentEntity);
        MatchEntity matchEntity = matchRepository.save(MatchEntity.builder()
                .id(matchId)
                .round(roundEntity)
                .participant2(participantEntity)
                .build()
        );
        matchService.submitMatchResult(
                userEntity,
                matchId,
                SubmitMatchResultRequestDto.builder()
                        .matchResult(MatchResult.WHITE_WIN)
                        .build()
        );
        MatchEntity matchAfter = matchRepository.findById(matchId).get();
        Assertions.assertNull(matchAfter.getResultSubmittedByParticipant1());
        Assertions.assertEquals(MatchResult.WHITE_WIN, matchAfter.getResultSubmittedByParticipant2());
        Assertions.assertNull(matchAfter.getResult());
    }



    @Test
    void testAsBlackAfterWhite() {
        UUID matchId = UUID.randomUUID();
        UserEntity userEntity = createDummyUser();
        TournamentEntity tournamentEntity = cerateDummyTournament();
        ParticipantEntity participantEntity = createParticipant(userEntity, tournamentEntity);
        RoundEntity roundEntity = createRound(tournamentEntity);
        MatchEntity matchEntity = matchRepository.save(MatchEntity.builder()
                .id(matchId)
                .round(roundEntity)
                .participant2(participantEntity)
                .resultSubmittedByParticipant1(MatchResult.WHITE_WIN)
                .build()
        );
        matchService.submitMatchResult(
                userEntity,
                matchId,
                SubmitMatchResultRequestDto.builder()
                        .matchResult(MatchResult.WHITE_WIN)
                        .build()
        );
        MatchEntity matchAfter = matchRepository.findById(matchId).get();
        Assertions.assertEquals(MatchResult.WHITE_WIN, matchAfter.getResultSubmittedByParticipant1());
        Assertions.assertEquals(MatchResult.WHITE_WIN, matchAfter.getResultSubmittedByParticipant2());
        Assertions.assertEquals(MatchResult.WHITE_WIN, matchAfter.getResult());
    }

    @Test
    void testAsBlackAfterWhiteConflict() {
        UUID matchId = UUID.randomUUID();
        UserEntity userEntity = createDummyUser();
        TournamentEntity tournamentEntity = cerateDummyTournament();
        ParticipantEntity participantEntity = createParticipant(userEntity, tournamentEntity);
        RoundEntity roundEntity = createRound(tournamentEntity);
        MatchEntity matchEntity = matchRepository.save(MatchEntity.builder()
                .id(matchId)
                .round(roundEntity)
                .participant2(participantEntity)
                .resultSubmittedByParticipant1(MatchResult.WHITE_WIN)
                .build()
        );
        matchService.submitMatchResult(
                userEntity,
                matchId,
                SubmitMatchResultRequestDto.builder()
                        .matchResult(MatchResult.BLACK_WIN)
                        .build()
        );
        MatchEntity matchAfter = matchRepository.findById(matchId).get();
        Assertions.assertEquals(MatchResult.WHITE_WIN, matchAfter.getResultSubmittedByParticipant1());
        Assertions.assertEquals(MatchResult.BLACK_WIN, matchAfter.getResultSubmittedByParticipant2());
        Assertions.assertNull(matchAfter.getResult());
    }

    private UserEntity createDummyUser() {
        UserEntity userEntity = userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .roles(List.of())
                .build());
        return userEntity;
    }

    private static RoundEntity createRound(TournamentEntity tournamentEntity) {
        RoundEntity roundEntity = RoundEntity.builder()
                .id(UUID.randomUUID())
                .tournament(tournamentEntity)
                .build();
        return roundEntity;
    }

    private ParticipantEntity createParticipant(UserEntity userEntity, TournamentEntity tournamentEntity) {
        ParticipantEntity participantEntity = participantRepository.save(ParticipantEntity.builder()
                .user(userEntity)
                .tournament(tournamentEntity)
                .build());
        return participantEntity;
    }

    private TournamentEntity cerateDummyTournament() {
        TournamentEntity tournamentEntity = tournamentRepository.save(TournamentEntity.builder().id(UUID.randomUUID()).build());
        return tournamentEntity;
    }

    @Test
    void testAsModerator() {
        UUID matchId = UUID.randomUUID();
        UserEntity userEntity = createDummyUser();
        TournamentEntity tournamentEntity = cerateDummyTournament();
        ParticipantEntity participantEntity = participantRepository.save(ParticipantEntity.builder()
                .user(userEntity)
                .tournament(tournamentEntity)
                .isModerator(true)
                .build());
        RoundEntity roundEntity = createRound(tournamentEntity);
        MatchEntity matchEntity = matchRepository.save(MatchEntity.builder()
                .id(matchId)
                .round(roundEntity)
                .build()
        );
        matchService.submitMatchResult(
                userEntity,
                matchId,
                SubmitMatchResultRequestDto.builder()
                        .matchResult(MatchResult.WHITE_WIN)
                        .build()
        );
        MatchEntity matchAfter = matchRepository.findById(matchId).get();
        Assertions.assertNull(matchAfter.getResultSubmittedByParticipant1());
        Assertions.assertNull(matchAfter.getResultSubmittedByParticipant2());
        Assertions.assertEquals(MatchResult.WHITE_WIN, matchAfter.getResult());
    }

    @Test
    void testAsOther() {
        UUID matchId = UUID.randomUUID();
        UserEntity userEntity = createDummyUser();
        TournamentEntity tournamentEntity = cerateDummyTournament();
        createParticipant(userEntity, tournamentEntity);
        RoundEntity roundEntity = createRound(tournamentEntity);
        MatchEntity matchEntity = matchRepository.save(MatchEntity.builder()
                .id(matchId)
                .round(roundEntity)
                .build()
        );
        Assertions.assertThrows(ResponseStatusException.class, () ->
                matchService.submitMatchResult(
                        userEntity,
                        matchId,
                        SubmitMatchResultRequestDto.builder()
                                .matchResult(MatchResult.WHITE_WIN)
                                .build()
                )
        );
        MatchEntity matchAfter = matchRepository.findById(matchId).get();
        Assertions.assertNull(matchAfter.getResultSubmittedByParticipant1());
        Assertions.assertNull(matchAfter.getResultSubmittedByParticipant2());
        Assertions.assertNull(matchAfter.getResult());
    }
}
