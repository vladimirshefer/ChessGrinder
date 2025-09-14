package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.chessengine.pairings.JaVaFoPairingStrategyImpl;
import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoundEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.repositories.MatchRepository;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.RoundRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.security.WithRefererOAuth2AuthorizationRequestResolver;
import com.chessgrinder.chessgrinder.util.Graph;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class RoundServiceTest {

    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private RoundService roundService;

    @SpyBean
    JaVaFoPairingStrategyImpl javaFoPairingStrategy;

    //These fields are needed for full SpringBootTest deployment
    @MockBean
    WithRefererOAuth2AuthorizationRequestResolver oauth2;
    @MockBean
    ClientRegistrationRepository clientReg;

    @Test
    void shouldCorrectlySetFinalPlacements() {
        TournamentEntity tournament = tournamentRepository.save(createTournament());

        ParticipantEntity p1 = participantRepository.save(createParticipant("Boogie", tournament));
        ParticipantEntity p2 = participantRepository.save(createParticipant("Pigster", tournament));
        ParticipantEntity p3 = participantRepository.save(createParticipant("In chess we trust", tournament));
        ParticipantEntity p4 = participantRepository.save(createParticipant("The SA", tournament));
        ParticipantEntity p5 = participantRepository.save(createParticipant("Alex", tournament));
        ParticipantEntity p6 = participantRepository.save(createParticipant("DmitryAnikeyev", tournament));
        ParticipantEntity p7 = participantRepository.save(createParticipant("Wolf", tournament));
        ParticipantEntity p8 = participantRepository.save(createParticipant("Magadan", tournament));
        ParticipantEntity p9 = participantRepository.save(createParticipant("Sultan", tournament));
        ParticipantEntity p10 = participantRepository.save(createParticipant("сэр А'ртур", tournament));
        ParticipantEntity p11 = participantRepository.save(createParticipant("Bob", tournament));
        ParticipantEntity p12 = participantRepository.save(createParticipant("milpops", tournament));
        ParticipantEntity p13 = participantRepository.save(createParticipant("если проиграю то мощно", tournament));
        ParticipantEntity p14 = participantRepository.save(createParticipant("Страх и ненависть на шахматах", tournament));

        RoundEntity round1 = roundRepository.save(createRound(tournament, 1));
        RoundEntity round2 = roundRepository.save(createRound(tournament, 2));
        RoundEntity round3 = roundRepository.save(createRound(tournament, 3));
        RoundEntity round4 = roundRepository.save(createRound(tournament, 4));
        RoundEntity round5 = roundRepository.save(createRound(tournament, 5));
        RoundEntity round6 = roundRepository.save(createRound(tournament, 6));

        //1 round
        matchRepository.save(createMatch(p1, p11, round1, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p6, p2, round1, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p14, p3, round1, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p4, p13, round1, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p5, p7, round1, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p8, p12, round1, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p10, null, round1, MatchResult.BUY));
        roundService.finishRound(tournament.getId(), 1);
        //2 round
        matchRepository.save(createMatch(p3, p1, round2, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p2, p14, round2, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p10, p4, round2, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p12, p5, round2, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p13, p6, round2, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p7, p8, round2, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p11, p9, round2, MatchResult.DRAW));
        roundService.finishRound(tournament.getId(), 2);
        //3 round
        matchRepository.save(createMatch(p5, p1, round3, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p4, p2, round3, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p3, p10, round3, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p6, p12, round3, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p9, p7, round3, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p8, p11, round3, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p14, p13, round3, MatchResult.BLACK_WIN));
        roundService.finishRound(tournament.getId(), 3);
        //4 round
        matchRepository.save(createMatch(p1, p2, round4, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p5, p3, round4, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p8, p4, round4, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p7, p6, round4, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p10, p9, round4, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p11, null, round4, MatchResult.BUY));
        matchRepository.save(createMatch(p12, null, round4, MatchResult.MISS));
        matchRepository.save(createMatch(p13, null, round4, MatchResult.MISS));
        matchRepository.save(createMatch(p14, null, round4, MatchResult.MISS));
        roundService.finishRound(tournament.getId(), 4);
        //5 round
        matchRepository.save(createMatch(p1, p6, round5, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p2, p3, round5, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p7, p4, round5, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p11, p5, round5, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p9, p8, round5, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p10, null, round5, MatchResult.BUY));
        matchRepository.save(createMatch(p12, null, round5, MatchResult.MISS));
        matchRepository.save(createMatch(p13, null, round5, MatchResult.MISS));
        matchRepository.save(createMatch(p14, null, round5, MatchResult.MISS));
        roundService.finishRound(tournament.getId(), 5);
        //6 round
        matchRepository.save(createMatch(p4, p1, round6, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p2, p5, round6, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p6, p3, round6, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p11, p7, round6, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p8, p10, round6, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p9, null, round6, MatchResult.BUY));
        matchRepository.save(createMatch(p12, null, round6, MatchResult.MISS));
        matchRepository.save(createMatch(p13, null, round6, MatchResult.MISS));
        matchRepository.save(createMatch(p14, null, round6, MatchResult.MISS));
        //Triggering recalculation method
        roundService.finishRound(tournament.getId(), 6);

        List<ParticipantEntity> sorted = participantRepository.findByTournamentId(tournament.getId()).stream()
                .sorted(Comparator.comparing(ParticipantEntity::getPlace))
                .toList();

        assertThat(sorted.get(0).getNickname()).isEqualTo("Boogie");
        assertThat(sorted.get(0).getScore()).isEqualByComparingTo(BigDecimal.valueOf(5.0));
        assertThat(sorted.get(1).getNickname()).isEqualTo("Pigster");
        assertThat(sorted.get(1).getScore()).isEqualByComparingTo(BigDecimal.valueOf(5.0));
        assertThat(sorted.get(2).getNickname()).isEqualTo("In chess we trust");
        assertThat(sorted.get(2).getScore()).isEqualByComparingTo(BigDecimal.valueOf(5.0));
        assertThat(sorted.get(3).getNickname()).isEqualTo("The SA");
        assertThat(sorted.get(3).getScore()).isEqualByComparingTo(BigDecimal.valueOf(4.0));
        assertThat(sorted.get(4).getNickname()).isEqualTo("Alex");
        assertThat(sorted.get(4).getScore()).isEqualByComparingTo(BigDecimal.valueOf(3.0));
        assertThat(sorted.get(5).getNickname()).isEqualTo("DmitryAnikeyev");
        assertThat(sorted.get(5).getScore()).isEqualByComparingTo(BigDecimal.valueOf(3.0));
        assertThat(sorted.get(6).getNickname()).isEqualTo("Wolf");
        assertThat(sorted.get(6).getScore()).isEqualByComparingTo(BigDecimal.valueOf(3.0));
        assertThat(sorted.get(7).getNickname()).isEqualTo("Magadan");
        assertThat(sorted.get(7).getScore()).isEqualByComparingTo(BigDecimal.valueOf(3.0));
        assertThat(sorted.get(8).getNickname()).isEqualTo("Sultan");
        assertThat(sorted.get(8).getScore()).isEqualByComparingTo(BigDecimal.valueOf(2.5));
        assertThat(sorted.get(9).getNickname()).isEqualTo("сэр А'ртур");
        assertThat(sorted.get(9).getScore()).isEqualByComparingTo(BigDecimal.valueOf(2.0));
        assertThat(sorted.get(10).getNickname()).isEqualTo("Bob");
        assertThat(sorted.get(10).getScore()).isEqualByComparingTo(BigDecimal.valueOf(1.5));
        assertThat(sorted.get(11).getNickname()).isEqualTo("milpops");
        assertThat(sorted.get(11).getScore()).isEqualByComparingTo(BigDecimal.valueOf(1.0));
        assertThat(sorted.get(12).getNickname()).isEqualTo("если проиграю то мощно");
        assertThat(sorted.get(12).getScore()).isEqualByComparingTo(BigDecimal.valueOf(1.0));
        assertThat(sorted.get(13).getNickname()).isEqualTo("Страх и ненависть на шахматах");
        assertThat(sorted.get(13).getScore()).isEqualByComparingTo(BigDecimal.valueOf(0.0));
    }

    /**
     * example: 1 > 2, 2 > 3, 3 > 1, 1 > 4
     * result: [1,2,3][4]
     */
    @Test
    void testPersonalEncounters() {
        ParticipantEntity p1 = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("p1").score(BigDecimal.valueOf(10)).buchholz(BigDecimal.valueOf(4)).build();
        ParticipantEntity p2 = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("p2").score(BigDecimal.valueOf(10)).buchholz(BigDecimal.valueOf(3)).build();
        ParticipantEntity p3 = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("p3").score(BigDecimal.valueOf(10)).buchholz(BigDecimal.valueOf(2)).build();
        ParticipantEntity p4 = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("p4").score(BigDecimal.valueOf(10)).buchholz(BigDecimal.valueOf(1)).build();
        var encounters = asList(
                new SimpleEntry<>(p1, p2),
                new SimpleEntry<>(p2, p3),
                new SimpleEntry<>(p3, p1),
                new SimpleEntry<>(p1, p4)
        );
        Collections.shuffle(encounters);
        Graph<ParticipantEntity> directEncounters = new Graph<>(encounters);
        Comparator<ParticipantEntity> participantEntityComparator = RoundService.compareParticipantEntityByPersonalEncounterWinnerFirst(directEncounters);
        assertEquals(0, participantEntityComparator.compare(p1, p2));
        assertEquals(0, participantEntityComparator.compare(p2, p1));
        assertEquals(0, participantEntityComparator.compare(p3, p1));
        assertEquals(0, participantEntityComparator.compare(p1, p3));
        assertEquals(-1, participantEntityComparator.compare(p1, p4));
        assertEquals(1, participantEntityComparator.compare(p4, p1));
        assertEquals(1, participantEntityComparator.compare(p4, p2));
        assertEquals(1, participantEntityComparator.compare(p4, p3));
        assertEquals(-1, participantEntityComparator.compare(p2, p4));
        assertEquals(-1, participantEntityComparator.compare(p3, p4));

        var participants = asList(p1, p2, p3, p4);
        for (int i = 0; i < 10; i++) { // many times to make sure that order does not matter
            Collections.shuffle(participants);
            participants.sort(participantEntityComparator);
            assertEquals(new HashSet<>(asList(p1, p2, p3)), new HashSet<>(participants.subList(0, 3)));
            assertEquals(new HashSet<>(asList(p4)), new HashSet<>(participants.subList(3,4)));
        }
    }

    @Test
    void testPairingsSave() {
        TournamentEntity tournament = tournamentRepository.save(createTournament());

        ParticipantEntity p1 = participantRepository.save(createParticipant("Journey", tournament));
        ParticipantEntity p2 = participantRepository.save(createParticipant("Hiloko", tournament));
        ParticipantEntity p3 = participantRepository.save(createParticipant("In chess we trust", tournament));
        ParticipantEntity p4 = participantRepository.save(createParticipant("The cluster arson", tournament));
        ParticipantEntity p5 = participantRepository.save(createParticipant("Alex", tournament));
        ParticipantEntity p6 = participantRepository.save(createParticipant("Dmitry", tournament));
        ParticipantEntity p7 = participantRepository.save(createParticipant("Hooray", tournament));
        ParticipantEntity p8 = participantRepository.save(createParticipant("Poliono", tournament));
        ParticipantEntity p9 = participantRepository.save(createParticipant("Jovoko", tournament));
        ParticipantEntity p10 = participantRepository.save(createParticipant("Masterpiece", tournament));
        ParticipantEntity p11 = participantRepository.save(createParticipant("Bob", tournament));
        ParticipantEntity p12 = participantRepository.save(createParticipant("milpops", tournament));
        ParticipantEntity p13 = participantRepository.save(createParticipant("Vasilyok", tournament));

        RoundEntity round1 = roundRepository.save(createRound(tournament, 1));
        Mockito.doAnswer(i -> new HashMap<Integer, Integer>() {{
            put(1, 2);
            put(3, 4);
            put(5, 6);
            put(7, 8);
            put(9, 10);
            put(11, 12);
            put(13, 0);
        }}).when(javaFoPairingStrategy).makePairings(Mockito.any());
        roundService.makePairings(tournament.getId(), 1);

        List<MatchEntity> matchEntitiesByRoundId = matchRepository.findMatchEntitiesByRoundId(round1.getId());

        assertEquals(7, matchEntitiesByRoundId.size());
    }

    private TournamentEntity createTournament() {
        return TournamentEntity.builder()
                .id(UUID.randomUUID())
                .status(TournamentStatus.ACTIVE)
                .name("Test Tournament")
                .roundsNumber(6)
                .pairingStrategy("SWISS")
                .build();
    }

    private ParticipantEntity createParticipant(String name, TournamentEntity tournament) {
        return ParticipantEntity.builder()
                .id(UUID.randomUUID())
                .nickname(name)
                .score(BigDecimal.valueOf(0))
                .buchholz(BigDecimal.valueOf(0))
                .tournament(tournament)
                .isMissing(false)
                .place(-1)
                .build();
    }

    private RoundEntity createRound(TournamentEntity tournament, int number) {
        return RoundEntity.builder()
                .id(UUID.randomUUID())
                .number(number)
                .isFinished(false)
                .tournament(tournament)
                .build();
    }

    private MatchEntity createMatch(ParticipantEntity p1, ParticipantEntity p2, RoundEntity round, MatchResult result) {
        return MatchEntity.builder()
                .id(UUID.randomUUID())
                .participant1(p1)
                .participant2(p2)
                .round(round)
                .result(result)
                .build();
    }
}
