package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.Player001TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.util.TrfUtil;
import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoundEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import com.chessgrinder.chessgrinder.chessengine.pairings.JaVaFoPairingStrategyImpl;

import static org.junit.jupiter.api.Assertions.*;

class TrfServiceTest {

    @Test
    void testToTrf() {
        TournamentEntity tournament = TournamentEntity.builder()
                .id(UUID.randomUUID())
                .roundsNumber(6)
                .build();
        ParticipantEntity participantJohnDoe = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("John Doe").initialEloPoints(1337).isModerator(true).score(BigDecimal.valueOf(7.0)).build();
        ParticipantEntity participantMichelKolos = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("Michel Kolos").initialEloPoints(2890).score(BigDecimal.valueOf(12.0)).build();
        ParticipantEntity participantKillianPotter = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("Killian Potter").initialEloPoints(400).score(BigDecimal.valueOf(2.0)).build();
        ParticipantEntity participantJozephMorskiy = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("Jozeph Mprskiy").initialEloPoints(950).score(BigDecimal.valueOf(5.0)).build();
        List<ParticipantEntity> participantEntities = List.of(
                participantJohnDoe,
                participantMichelKolos,
                participantKillianPotter,
                participantJozephMorskiy
        );

        RoundEntity round1 = RoundEntity.builder().tournament(tournament).number(1).isFinished(true).build();
        List<MatchEntity> matches1 = List.of(
                MatchEntity.builder().round(round1).participant1(participantJohnDoe).participant2(participantMichelKolos).result(MatchResult.WHITE_WIN).build()
        );
        round1.setMatches(matches1);
        tournament.getRounds().add(round1);

        RoundEntity round2 = RoundEntity.builder().tournament(tournament).number(2).isFinished(true).build();
        List<MatchEntity> matches2 = List.of(
                MatchEntity.builder().round(round2).participant1(participantJozephMorskiy).participant2(participantJohnDoe).result(MatchResult.DRAW).build(),
                MatchEntity.builder().round(round2).participant1(participantKillianPotter).participant2(null).result(MatchResult.BUY).build()
        );
        round2.setMatches(matches2);
        tournament.getRounds().add(round2);

        List<TrfLine> trfTournament = TrfService.toTrfTournament(participantEntities, tournament);
        assertEquals("""
                XXR 6
                001    1      John Doe                          1337                             7.0          2 w 1     4 b = \s
                001    2      Michel Kolos                      2890                             12.0         1 b 0  0000 - Z \s
                001    3      Killian Potter                     400                             2.0       0000 - Z  0000 - U \s
                001    4      Jozeph Mprskiy                     950                             5.0       0000 - Z     1 w = \s
                """, TrfUtil.writeTrfLines(trfTournament));
    }

    @Test
    void testEmpty() {
        TournamentEntity tournament = TournamentEntity.builder()
                .rounds(Collections.emptyList())
                .build();
        List<ParticipantEntity> participantEntities = List.of();
        List<TrfLine> trfTournament = TrfService.toTrfTournament(participantEntities, tournament);
        assertEquals("", TrfUtil.writeTrfLines(trfTournament));
    }

    @Test
    void testLateEntrantJavafoFailure() {
        TournamentEntity tournament = TournamentEntity.builder()
                .id(UUID.randomUUID())
                .roundsNumber(3)
                .rounds(new ArrayList<>())
                .build();

        ParticipantEntity participantA = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("A").initialEloPoints(1500).score(BigDecimal.valueOf(1.0)).build();
        ParticipantEntity participantB = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("B").initialEloPoints(1400).score(BigDecimal.valueOf(0.0)).build();
        ParticipantEntity participantC = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("C").initialEloPoints(1300).score(BigDecimal.valueOf(1.0)).build();
        ParticipantEntity participantD = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("D").initialEloPoints(1200).score(BigDecimal.valueOf(0.0)).build();

        RoundEntity round1 = RoundEntity.builder().tournament(tournament).number(1).isFinished(true).build();
        List<MatchEntity> matches1 = List.of(
                MatchEntity.builder().round(round1).participant1(participantA).participant2(participantB).result(MatchResult.WHITE_WIN).build(),
                MatchEntity.builder().round(round1).participant1(participantC).participant2(participantD).result(MatchResult.WHITE_WIN).build()
        );
        round1.setMatches(matches1);
        tournament.getRounds().add(round1);

        // Add late entrant E
        ParticipantEntity participantE = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("E").initialEloPoints(1600).score(BigDecimal.ZERO).build();

        List<ParticipantEntity> participantEntities = List.of(
                participantE, // index 0 (Rank 1)
                participantA, // index 1 (Rank 2)
                participantB, // index 2 (Rank 3)
                participantC, // index 3 (Rank 4)
                participantD  // index 4 (Rank 5)
        );

        // Pair Round 2
        List<TrfLine> trfTournament2 = TrfService.toTrfTournament(participantEntities, tournament);
        var pairings2 = new JaVaFoPairingStrategyImpl().makePairings(trfTournament2);
        
        assertNotNull(pairings2);
        assertEquals(3, pairings2.size());
        assertEquals(Map.of(4, 2, 3, 1, 5, 0), pairings2);

        // Now simulate playing Round 2 and finishing it
        // Pairings: {4=2, 3=1, 5=0} (C vs A, B vs E, D vs Bye)
        // Let's say:
        // C vs A -> C wins (WHITE_WIN) -> C score = 2.0, A score = 1.0
        // B vs E -> E wins (BLACK_WIN) -> E score = 1.0, B score = 0.0
        // D vs Bye -> D gets bye (BUY) -> D score = 1.0
        participantC.setScore(BigDecimal.valueOf(2.0));
        participantA.setScore(BigDecimal.valueOf(1.0));
        participantE.setScore(BigDecimal.valueOf(1.0));
        participantB.setScore(BigDecimal.valueOf(0.0));
        participantD.setScore(BigDecimal.valueOf(1.0));

        RoundEntity round2 = RoundEntity.builder().tournament(tournament).number(2).isFinished(true).build();
        List<MatchEntity> matches2 = List.of(
                MatchEntity.builder().round(round2).participant1(participantC).participant2(participantA).result(MatchResult.WHITE_WIN).build(),
                MatchEntity.builder().round(round2).participant1(participantB).participant2(participantE).result(MatchResult.BLACK_WIN).build(),
                MatchEntity.builder().round(round2).participant1(participantD).participant2(null).result(MatchResult.BUY).build()
        );
        round2.setMatches(matches2);
        tournament.getRounds().add(round2);

        // Now pair Round 3
        List<TrfLine> trfTournament3 = TrfService.toTrfTournament(participantEntities, tournament);

        var pairings3 = new JaVaFoPairingStrategyImpl().makePairings(trfTournament3);
        assertNotNull(pairings3);
        assertEquals(3, pairings3.size());
        assertTrue(pairings3.containsValue(0));
    }

    @Test
    void testNewcomerGapFillingDoesNotCreateNulls() {
        TournamentEntity tournament = TournamentEntity.builder()
                .id(UUID.randomUUID())
                .roundsNumber(3)
                .rounds(new ArrayList<>())
                .build();

        ParticipantEntity participantA = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("NewcomerA").initialEloPoints(1500).build();
        ParticipantEntity participantB = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("NewcomerB").initialEloPoints(1400).build();
        ParticipantEntity participantC = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("NewcomerC").initialEloPoints(1300).build();
        ParticipantEntity participantD = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("NewcomerD").initialEloPoints(1200).build();

        // Round 1
        RoundEntity round1 = RoundEntity.builder().tournament(tournament).number(1).isFinished(true).build();
        round1.setMatches(List.of(
                MatchEntity.builder().round(round1).participant1(participantA).participant2(participantB).result(MatchResult.WHITE_WIN).build(),
                MatchEntity.builder().round(round1).participant1(participantC).participant2(participantD).result(MatchResult.WHITE_WIN).build()
        ));
        tournament.getRounds().add(round1);

        // Add E before Round 2 (so E did not play Round 1)
        ParticipantEntity participantE = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("NewcomerE").initialEloPoints(1100).build();

        // Round 2
        RoundEntity round2 = RoundEntity.builder().tournament(tournament).number(2).isFinished(true).build();
        round2.setMatches(List.of(
                MatchEntity.builder().round(round2).participant1(participantA).participant2(participantC).result(MatchResult.WHITE_WIN).build(),
                MatchEntity.builder().round(round2).participant1(participantB).participant2(participantE).result(MatchResult.WHITE_WIN).build(),
                MatchEntity.builder().round(round2).participant1(participantD).participant2(null).result(MatchResult.BUY).build()
        ));
        tournament.getRounds().add(round2);

        // Add F before Round 3 (so F did not play Round 1 and Round 2)
        ParticipantEntity participantF = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("NewcomerF").initialEloPoints(1000).build();

        List<ParticipantEntity> participantEntities = List.of(
                participantA, participantB, participantC, participantD, participantE, participantF
        );

        // This should not throw NullPointerException and should have all matches populated (no nulls in the lists)
        List<TrfLine> trfLines = TrfService.toTrfTournament(participantEntities, tournament);

        // Verify that serialization works successfully without NPE
        String trfString = TrfUtil.writeTrfLines(trfLines);
        assertFalse(trfString.isEmpty());
        assertTrue(trfString.contains("NewcomerE"));
        assertTrue(trfString.contains("NewcomerF"));

        // Check that E and F match lists do not contain nulls
        for (TrfLine line : trfLines) {
            if (line instanceof Player001TrfLine playerLine) {
                List<Player001TrfLine.Match> matches = playerLine.getMatches();
                assertNotNull(matches);
                for (Player001TrfLine.Match match : matches) {
                    assertNotNull(match, "Match should not be null for player " + playerLine.getName());
                }
            }
        }
    }
}
