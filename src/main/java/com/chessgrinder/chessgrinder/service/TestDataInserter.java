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
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Profile("local")
@Component
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

        User userTest1 = userRepository.save(User.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000001")).name("userTest1").build());
        User userTest2 = userRepository.save(User.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000002")).name("userTest2").build());
        User userTest3 = userRepository.save(User.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000003")).name("userTest3").build());
        User userTest4 = userRepository.save(User.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000004")).name("userTest4").build());
        User userTest5 = userRepository.save(User.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000005")).name("userTest5").build());
        User userTest6 = userRepository.save(User.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000006")).name("userTest6").build());
        User userTest7 = userRepository.save(User.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000007")).name("userTest7").build());
        User userTest8 = userRepository.save(User.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000008")).name("userTest8").build());
        User userTest9 = userRepository.save(User.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000009")).name("userTest9").build());
        User userTest10 = userRepository.save(User.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000010")).name("userTest10").build());
        User userTest11 = userRepository.save(User.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000011")).name("userTest11").build());
        User userTest12 = userRepository.save(User.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000012")).name("userTest12").build());
        User userTest13 = userRepository.save(User.builder().id(UUID.fromString("aaaa1111-1111-1111-1111-000000000013")).name("userTest13").build());



        Badge badge1 = badgeRepository.save(Badge.builder().id(UUID.randomUUID())
                .description("For 300 lari donation!")
                .title("300 lari")
                .pictureUrl("🐝")
                .build());

        Badge badge2 = badgeRepository.save(Badge.builder().id(UUID.randomUUID())
                .description("Win 3 tournaments in tryhard league!")
                .title("3 wins in tryhard")
                .pictureUrl("🦀")
                .build());

        UserBadge userBadge1 = userBadgeRepository.save(UserBadge.builder().id(UUID.fromString("aaaa3333-1111-1111-1111-000000000001")).user(userTest1).badge(badge1).build());
        UserBadge userBadge2 = userBadgeRepository.save(UserBadge.builder().id(UUID.fromString("aaaa3333-1111-1111-1111-000000000002")).user(userTest2).badge(badge2).build());
        UserBadge userBadge3 = userBadgeRepository.save(UserBadge.builder().id(UUID.fromString("aaaa3333-1111-1111-1111-000000000003")).user(userTest3).badge(badge1).build());
        UserBadge userBadge4 = userBadgeRepository.save(UserBadge.builder().id(UUID.fromString("aaaa3333-1111-1111-1111-000000000004")).user(userTest4).badge(badge2).build());

        Tournament tournament1 = tournamentRepository.save(Tournament.builder().id(UUID.fromString("aaaa4444-1111-1111-1111-000000000001")).date(LocalDateTime.now()).status(TournamentStatus.ACTIVE).build());
        Tournament tournament2 = tournamentRepository.save(Tournament.builder().id(UUID.fromString("aaaa4444-1111-1111-1111-000000000002")).date(LocalDateTime.now()).status(TournamentStatus.FINISHED).build());
        Tournament tournament3 = tournamentRepository.save(Tournament.builder().id(UUID.fromString("aaaa4444-1111-1111-1111-000000000003")).date(LocalDateTime.now()).status(TournamentStatus.PLANNED).build());
        Tournament tournament4 = tournamentRepository.save(Tournament.builder().id(UUID.fromString("aaaa4444-1111-1111-1111-000000000004")).date(LocalDateTime.now()).status(TournamentStatus.FINISHED).build());
        Tournament tournament5 = tournamentRepository.save(Tournament.builder().id(UUID.fromString("aaaa4444-1111-1111-1111-000000000005")).date(LocalDateTime.now()).status(TournamentStatus.FINISHED).build());
        Tournament tournament6 = tournamentRepository.save(Tournament.builder().id(UUID.fromString("aaaa4444-1111-1111-1111-000000000006")).date(LocalDateTime.now()).status(TournamentStatus.PLANNED).build());

        //-------------------------------------------------------------------------------------------

        Participant participant1 = participantRepository.save(Participant.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000001"))
                .nickname("participantTEST1")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userTest1)
                .tournament(tournament1)
                .build());

        Participant participant2 = participantRepository.save(Participant.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000002"))
                .nickname("participantTEST2")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userTest2)
                .tournament(tournament1)
                .build());

        Participant participant3 = participantRepository.save(Participant.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000003"))
                .nickname("participantTEST3")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userTest3)
                .tournament(tournament1)
                .build());

        Participant participant4 = participantRepository.save(Participant.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000004"))
                .nickname("participantTEST4")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userTest4)
                .tournament(tournament1)
                .build());

        Participant participant5 = participantRepository.save(Participant.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000005"))
                .nickname("participantTEST5")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userTest5)
                .tournament(tournament1)
                .build());

        Participant participant6 = participantRepository.save(Participant.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000006"))
                .nickname("participantTEST6")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userTest6)
                .tournament(tournament1)
                .build());

        Participant participant7 = participantRepository.save(Participant.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000007"))
                .nickname("participantTEST7")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userTest7)
                .tournament(tournament1)
                .build());

        Participant participant8 = participantRepository.save(Participant.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000008"))
                .nickname("participantTEST8")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userTest8)
                .tournament(tournament1)
                .build());

        Participant participant9 = participantRepository.save(Participant.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000009"))
                .nickname("participantTEST9")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userTest9)
                .tournament(tournament1)
                .build());

        Participant participant10 = participantRepository.save(Participant.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000010"))
                .nickname("participantTEST10")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userTest10)
                .tournament(tournament1)
                .build());

        Participant participant11 = participantRepository.save(Participant.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000011"))
                .nickname("participantTEST11")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userTest11)
                .tournament(tournament1)
                .build());

        Participant participant12 = participantRepository.save(Participant.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000012"))
                .nickname("participantTEST12")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userTest12)
                .tournament(tournament1)
                .build());

        Participant participant13 = participantRepository.save(Participant.builder()
                .id(UUID.fromString("aaaa5555-1111-1111-1111-000000000013"))
                .nickname("participantTEST13")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(userTest13)
                .tournament(tournament1)
                .build());


        Match match1 = matchRepository.save(
                Match.builder()
                        .id(UUID.fromString("aaaa7777-1111-1111-1111-000000000001"))
                        .participant1(participant1)
                        .participant2(participant2)
                        .result(MatchResult.WHITE_WIN)
                        .build()
        );

        Match match2 = matchRepository.save(
                Match.builder()
                        .id(UUID.fromString("aaaa6666-1111-1111-1111-000000000002"))
                        .participant1(participant3)
                        .participant2(participant4)
                        .result(MatchResult.BLACK_WIN)
                        .build()
        );


        //-------------------------------------------------------------------------------------------

        Round round1 = roundRepository.save(
                Round.builder()
                        .id(UUID.fromString("aaaa6666-1111-1111-1111-000000000001"))
                        .tournament(tournament1)
                        .number(100)
                        .isFinished(true)
                        .matches(List.of(match1, match2))
                        .build()
        );

        match1.setRound(round1);
        match2.setRound(round1);

        matchRepository.save(match1);
        matchRepository.save(match2);

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
