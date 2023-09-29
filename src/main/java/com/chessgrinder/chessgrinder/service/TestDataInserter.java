package com.chessgrinder.chessgrinder.service;

import java.math.*;
import java.time.*;
import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.boot.context.event.*;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.*;

@Profile("local")
/*
@Component
*/
@RequiredArgsConstructor
public class TestDataInserter {

    final UserRepository userRepository;
    final BadgeRepository badgeRepository;
    final UserBadgeRepository userBadgeRepository;
    final TournamentRepository tournamentRepository;
    final ParticipantRepository participantRepository;
    final MatchRepository matchRepository;
    final RoundRepository roundRepository;

    @EventListener(ApplicationStartedEvent.class)
    @Transactional
    public void init() {

        UserEntity userEntityTest1 = userRepository.save(UserEntity.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000001")).name("userTest1").build());
        UserEntity userEntityTest2 = userRepository.save(UserEntity.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000002")).name("userTest2").build());
        UserEntity userEntityTest3 = userRepository.save(UserEntity.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000003")).name("userTest3").build());
        UserEntity userEntityTest4 = userRepository.save(UserEntity.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000004")).name("userTest4").build());
        UserEntity userEntityTest5 = userRepository.save(UserEntity.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000005")).name("userTest5").build());
        UserEntity userEntityTest6 = userRepository.save(UserEntity.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000006")).name("userTest6").build());
        UserEntity userEntityTest7 = userRepository.save(UserEntity.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000007")).name("userTest7").build());
        UserEntity userEntityTest8 = userRepository.save(UserEntity.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000008")).name("userTest8").build());
        UserEntity userEntityTest9 = userRepository.save(UserEntity.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000009")).name("userTest9").build());
        UserEntity userEntityTest10 = userRepository.save(UserEntity.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000010")).name("userTest10").build());
        UserEntity userEntityTest11 = userRepository.save(UserEntity.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000011")).name("userTest11").build());
        UserEntity userEntityTest12 = userRepository.save(UserEntity.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000012")).name("userTest12").build());
        UserEntity userEntityTest13 = userRepository.save(UserEntity.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000013")).name("userTest13").build());



        BadgeEntity badgeEntity1 = badgeRepository.save(BadgeEntity.builder().id(UUID.randomUUID())
                .description("For 300 lari donation!")
                .title("300 lari")
                .pictureUrl("🐝")
                .build());

        BadgeEntity badgeEntity2 = badgeRepository.save(BadgeEntity.builder().id(UUID.randomUUID())
                .description("Win 3 tournaments in tryhard league!")
                .title("3 wins in tryhard")
                .pictureUrl("🦀")
                .build());

        UserBadgeEntity userBadgeEntity1 = userBadgeRepository.save(UserBadgeEntity.builder().id(UUID.fromString("aaaa3333-1111-1111-1111-000000000001")).user(userEntityTest1).badge(badgeEntity1).build());
        UserBadgeEntity userBadgeEntity2 = userBadgeRepository.save(UserBadgeEntity.builder().id(UUID.fromString("aaaa3333-1111-1111-1111-000000000002")).user(userEntityTest2).badge(badgeEntity2).build());
        UserBadgeEntity userBadgeEntity3 = userBadgeRepository.save(UserBadgeEntity.builder().id(UUID.fromString("aaaa3333-1111-1111-1111-000000000003")).user(userEntityTest3).badge(badgeEntity1).build());
        UserBadgeEntity userBadgeEntity4 = userBadgeRepository.save(UserBadgeEntity.builder().id(UUID.fromString("aaaa3333-1111-1111-1111-000000000004")).user(userEntityTest4).badge(badgeEntity2).build());

        TournamentEntity tournamentEntity1 = tournamentRepository.save(TournamentEntity.builder().id(UUID.fromString("aaaa4444-1111-1111-1111-000000000001")).date(LocalDateTime.now()).status(TournamentStatus.ACTIVE).build());
        TournamentEntity tournamentEntity2 = tournamentRepository.save(TournamentEntity.builder().id(UUID.fromString("aaaa4444-1111-1111-1111-000000000002")).date(LocalDateTime.now()).status(TournamentStatus.FINISHED).build());
        TournamentEntity tournamentEntity3 = tournamentRepository.save(TournamentEntity.builder().id(UUID.fromString("aaaa4444-1111-1111-1111-000000000003")).date(LocalDateTime.now()).status(TournamentStatus.PLANNED).build());
        TournamentEntity tournamentEntity4 = tournamentRepository.save(TournamentEntity.builder().id(UUID.fromString("aaaa4444-1111-1111-1111-000000000004")).date(LocalDateTime.now()).status(TournamentStatus.FINISHED).build());
        TournamentEntity tournamentEntity5 = tournamentRepository.save(TournamentEntity.builder().id(UUID.fromString("aaaa4444-1111-1111-1111-000000000005")).date(LocalDateTime.now()).status(TournamentStatus.FINISHED).build());
        TournamentEntity tournamentEntity6 = tournamentRepository.save(TournamentEntity.builder().id(UUID.fromString("aaaa4444-1111-1111-1111-000000000006")).date(LocalDateTime.now()).status(TournamentStatus.PLANNED).build());

        //-------------------------------------------------------------------------------------------

        ParticipantEntity participantEntity1 = participantRepository.save(ParticipantEntity.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000001"))
                .nickname("participantTEST1")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userEntityTest1)
                .tournament(tournamentEntity1)
                .build());

        ParticipantEntity participantEntity2 = participantRepository.save(ParticipantEntity.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000002"))
                .nickname("participantTEST2")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userEntityTest2)
                .tournament(tournamentEntity1)
                .build());

        ParticipantEntity participantEntity3 = participantRepository.save(ParticipantEntity.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000003"))
                .nickname("participantTEST3")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userEntityTest3)
                .tournament(tournamentEntity1)
                .build());

        ParticipantEntity participantEntity4 = participantRepository.save(ParticipantEntity.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000004"))
                .nickname("participantTEST4")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userEntityTest4)
                .tournament(tournamentEntity1)
                .build());

        ParticipantEntity participantEntity5 = participantRepository.save(ParticipantEntity.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000005"))
                .nickname("participantTEST5")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userEntityTest5)
                .tournament(tournamentEntity1)
                .build());

        ParticipantEntity participantEntity6 = participantRepository.save(ParticipantEntity.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000006"))
                .nickname("participantTEST6")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userEntityTest6)
                .tournament(tournamentEntity1)
                .build());

        ParticipantEntity participantEntity7 = participantRepository.save(ParticipantEntity.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000007"))
                .nickname("participantTEST7")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userEntityTest7)
                .tournament(tournamentEntity1)
                .build());

        ParticipantEntity participantEntity8 = participantRepository.save(ParticipantEntity.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000008"))
                .nickname("participantTEST8")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userEntityTest8)
                .tournament(tournamentEntity1)
                .build());

        ParticipantEntity participantEntity9 = participantRepository.save(ParticipantEntity.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000009"))
                .nickname("participantTEST9")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userEntityTest9)
                .tournament(tournamentEntity1)
                .build());

        ParticipantEntity participantEntity10 = participantRepository.save(ParticipantEntity.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000010"))
                .nickname("participantTEST10")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userEntityTest10)
                .tournament(tournamentEntity1)
                .build());

        ParticipantEntity participantEntity11 = participantRepository.save(ParticipantEntity.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000011"))
                .nickname("participantTEST11")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userEntityTest11)
                .tournament(tournamentEntity1)
                .build());

        ParticipantEntity participantEntity12 = participantRepository.save(ParticipantEntity.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000012"))
                .nickname("participantTEST12")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userEntityTest12)
                .tournament(tournamentEntity1)
                .build());

        ParticipantEntity participantEntity13 = participantRepository.save(ParticipantEntity.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000013"))
                .nickname("participantTEST13")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userEntityTest13)
                .tournament(tournamentEntity1)
                .build());


        MatchEntity matchEntity1 = matchRepository.save(
                MatchEntity.builder()
                        .id(UUID.fromString("aaaa7777-1111-1111-1111-000000000001"))
                        .participant1(participantEntity1)
                        .participant2(participantEntity2)
                        .result(MatchResult.WHITE_WIN)
                        .build()
        );

        MatchEntity matchEntity2 = matchRepository.save(
                MatchEntity.builder()
                        .id(UUID.fromString("aaaa6666-1111-1111-1111-000000000002"))
                        .participant1(participantEntity3)
                        .participant2(participantEntity4)
                        .result(MatchResult.BLACK_WIN)
                        .build()
        );


        //-------------------------------------------------------------------------------------------

        RoundEntity roundEntity1 = roundRepository.save(
                RoundEntity.builder()
                        .id(UUID.fromString("aaaa6666-1111-1111-1111-000000000001"))
                        .tournament(tournamentEntity1)
                        .number(100)
                        .isFinished(true)
                        .matches(List.of(matchEntity1, matchEntity2))
                        .build()
        );

        matchEntity1.setRound(roundEntity1);
        matchEntity2.setRound(roundEntity1);

        matchRepository.save(matchEntity1);
        matchRepository.save(matchEntity2);

/*
        Round round1 = roundRepository.save(
                Round.builder()
                        .id(UUID.randomUUID())
                        .tournament(tournament1)
                        .number(1)
                        .isFinished(true)
                        .matches(List.of(
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .participant1(participant1)
                                                .participant2(participant2)
                                                .result(MatchResult.WHITE_WIN)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .participant1(participant3)
                                                .participant2(participant4)
                                                .result(MatchResult.BLACK_WIN)
                                                .build()
                                )
                        ))
                        .build()
        );

        Round round2 = roundRepository.save(
                Round.builder()
                        .id(UUID.randomUUID())
                        .tournament(tournament1)
                        .number(2)
                        .isFinished(true)
                        .matches(List.of(
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .participant1(participant1)
                                                .participant2(participant2)
                                                .result(MatchResult.WHITE_WIN)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .participant1(participant3)
                                                .participant2(participant4)
                                                .result(MatchResult.BLACK_WIN)
                                                .build()
                                ),
                                matchRepository.save(Match.builder()
                                        .id(UUID.randomUUID())
                                        .participant1(participant1)
                                        .participant2(participant3)
                                        .result(MatchResult.DRAW)
                                        .build()
                                ),
                                matchRepository.save(Match.builder()
                                        .id(UUID.randomUUID())
                                        .participant1(participant3)
                                        .participant2(participant4)
                                        .result(MatchResult.WHITE_WIN)
                                        .build()
                                )

                        ))
                        .build()
        );


        Round round3 = roundRepository.save(
                Round.builder()
                        .id(UUID.randomUUID())
                        .tournament(tournament1)
                        .number(3)
                        .isFinished(true)
                        .matches(List.of(
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .participant1(participant1)
                                                .participant2(participant2)
                                                .result(MatchResult.WHITE_WIN)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .participant1(participant3)
                                                .participant2(participant4)
                                                .result(MatchResult.BLACK_WIN)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .participant1(participant4)
                                                .participant2(participant2)
                                                .result(MatchResult.WHITE_WIN)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .participant1(participant3)
                                                .participant2(participant1)
                                                .result(MatchResult.DRAW)
                                                .build()
                                )
                        ))
                        .build()
        );


        Round round4 = roundRepository.save(
                Round.builder()
                        .id(UUID.randomUUID())
                        .tournament(tournament1)
                        .number(4)
                        .isFinished(true)
                        .matches(List.of(
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .participant1(participant3)
                                                .participant2(participant4)
                                                .result(MatchResult.WHITE_WIN)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .participant1(participant2)
                                                .participant2(participant4)
                                                .result(MatchResult.BLACK_WIN)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .participant1(participant3)
                                                .participant2(participant1)
                                                .result(MatchResult.DRAW)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .participant1(participant3)
                                                .participant2(participant2)
                                                .result(MatchResult.BLACK_WIN)
                                                .build()
                                )
                        ))
                        .build()
        ); */
    }
}
