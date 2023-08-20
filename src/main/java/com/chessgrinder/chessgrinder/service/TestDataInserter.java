package com.chessgrinder.chessgrinder.service;

import java.math.*;
import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.boot.context.event.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

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

        User user1 = userRepository.save(User.builder().id(UUID.randomUUID()).name("Vladimir Shefer").build());
        User user2 = userRepository.save(User.builder().id(UUID.randomUUID()).name("Alexander Boldyrev").build());
        User user3 = userRepository.save(User.builder().id(UUID.randomUUID()).name("Statislav Malov").build());
        User user4 = userRepository.save(User.builder().id(UUID.randomUUID()).name("Malik Rezaev").build());

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

        UserBadge userBadge1 = userBadgeRepository.save(UserBadge.builder().id(UUID.randomUUID()).user(user1).badge(badge1).build());
        UserBadge userBadge2 = userBadgeRepository.save(UserBadge.builder().id(UUID.randomUUID()).user(user4).badge(badge2).build());
        UserBadge userBadge3 = userBadgeRepository.save(UserBadge.builder().id(UUID.randomUUID()).user(user3).badge(badge1).build());
        UserBadge userBadge4 = userBadgeRepository.save(UserBadge.builder().id(UUID.randomUUID()).user(user3).badge(badge2).build());

        Tournament tournament1 = tournamentRepository.save(Tournament.builder().id(UUID.randomUUID()).date(new Date()).status(TournamentStatus.ACTIVE).build());
        Tournament tournament2 = tournamentRepository.save(Tournament.builder().id(UUID.randomUUID()).date(new Date()).status(TournamentStatus.FINISHED).build());
        Tournament tournament3 = tournamentRepository.save(Tournament.builder().id(UUID.randomUUID()).date(new Date()).status(TournamentStatus.PLANNED).build());
        Tournament tournament4 = tournamentRepository.save(Tournament.builder().id(UUID.randomUUID()).date(new Date()).status(TournamentStatus.FINISHED).build());
        Tournament tournament5 = tournamentRepository.save(Tournament.builder().id(UUID.randomUUID()).date(new Date()).status(TournamentStatus.FINISHED).build());
        Tournament tournament6 = tournamentRepository.save(Tournament.builder().id(UUID.randomUUID()).date(new Date()).status(TournamentStatus.PLANNED).build());

        Participant participant1 = participantRepository.save(Participant.builder()
                .id(UUID.randomUUID())
                .nickname("Kryzhovnik")
                .score(BigDecimal.ONE)
                .buchholz(BigDecimal.valueOf(18.5))
                .user(user1)
                .tournament(tournament1)
                .build());

        Participant participant2 = participantRepository.save(Participant.builder()
                .id(UUID.randomUUID())
                .nickname("Cheremuha")
                .score(BigDecimal.valueOf(3))
                .buchholz(BigDecimal.valueOf(19.5))
                .user(user2)
                .tournament(tournament1)
                .build());

        Participant participant3 = participantRepository.save(Participant.builder()
                .id(UUID.randomUUID())
                .nickname("Klop Koldun")
                .score(BigDecimal.valueOf(6))
                .buchholz(BigDecimal.valueOf(12.5))
                .user(user3)
                .tournament(tournament1)
                .build());

        Participant participant4 = participantRepository.save(Participant.builder()
                .id(UUID.randomUUID())
                .nickname("Lisichka Sestrichka")
                .score(BigDecimal.valueOf(1))
                .buchholz(BigDecimal.valueOf(1.5))
                .user(user4)
                .tournament(tournament1)
                .build());

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
                                                .user1(user1)
                                                .user2(user2)
                                                .result(MatchResult.WHITE_WIN)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .user1(user3)
                                                .user2(user4)
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
                                                .user1(user1)
                                                .user2(user2)
                                                .result(MatchResult.WHITE_WIN)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .user1(user3)
                                                .user2(user4)
                                                .result(MatchResult.BLACK_WIN)
                                                .build()
                                ),
                                matchRepository.save(Match.builder()
                                        .id(UUID.randomUUID())
                                        .user1(user1)
                                        .user2(user3)
                                        .result(MatchResult.DRAW)
                                        .build()
                                ),
                                matchRepository.save(Match.builder()
                                        .id(UUID.randomUUID())
                                        .user1(user3)
                                        .user2(user4)
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
                                                .user1(user1)
                                                .user2(user2)
                                                .result(MatchResult.WHITE_WIN)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .user1(user3)
                                                .user2(user4)
                                                .result(MatchResult.BLACK_WIN)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .user1(user4)
                                                .user2(user2)
                                                .result(MatchResult.WHITE_WIN)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .user1(user3)
                                                .user2(user1)
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
                                                .user1(user3)
                                                .user2(user4)
                                                .result(MatchResult.WHITE_WIN)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .user1(user2)
                                                .user2(user4)
                                                .result(MatchResult.BLACK_WIN)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .user1(user3)
                                                .user2(user1)
                                                .result(MatchResult.DRAW)
                                                .build()
                                ),
                                matchRepository.save(
                                        Match.builder()
                                                .id(UUID.randomUUID())
                                                .user1(user3)
                                                .user2(user2)
                                                .result(MatchResult.BLACK_WIN)
                                                .build()
                                )
                        ))
                        .build()
        );
    }
}
