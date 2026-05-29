package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.chessengine.pairings.JaVaFoPairingStrategyImpl;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

class LateEntrantPairingTest {

    @Test
    void testLateEntrantAtRound4Pairing() {
        TournamentEntity tournament = TournamentEntity.builder()
                .id(UUID.randomUUID())
                .roundsNumber(4)
                .rounds(new ArrayList<>())
                .build();

        ParticipantEntity participantA = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("Round4A").initialEloPoints(1500).score(BigDecimal.valueOf(3.0)).build();
        ParticipantEntity participantB = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("Round4B").initialEloPoints(1400).score(BigDecimal.valueOf(0.0)).build();
        ParticipantEntity participantC = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("Round4C").initialEloPoints(1300).score(BigDecimal.valueOf(2.0)).build();
        ParticipantEntity participantD = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("Round4D").initialEloPoints(1200).score(BigDecimal.valueOf(2.0)).build();
        ParticipantEntity participantE = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("Round4E").initialEloPoints(1100).score(BigDecimal.valueOf(2.0)).build();
        ParticipantEntity participantF = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("Round4F").initialEloPoints(1000).score(BigDecimal.valueOf(0.0)).build();

        // Round 1
        // A vs B (A wins) -> A:1, B:0
        // C vs D (C wins) -> C:1, D:0
        // E vs F (E wins) -> E:1, F:0
        RoundEntity round1 = RoundEntity.builder().tournament(tournament).number(1).isFinished(true).build();
        round1.setMatches(List.of(
                MatchEntity.builder().round(round1).participant1(participantA).participant2(participantB).result(MatchResult.WHITE_WIN).build(),
                MatchEntity.builder().round(round1).participant1(participantC).participant2(participantD).result(MatchResult.WHITE_WIN).build(),
                MatchEntity.builder().round(round1).participant1(participantE).participant2(participantF).result(MatchResult.WHITE_WIN).build()
        ));
        tournament.getRounds().add(round1);

        // Round 2
        // A vs C (A wins) -> A:2, C:1
        // E vs B (E wins) -> E:2, B:0
        // D vs F (D wins) -> D:1, F:0
        RoundEntity round2 = RoundEntity.builder().tournament(tournament).number(2).isFinished(true).build();
        round2.setMatches(List.of(
                MatchEntity.builder().round(round2).participant1(participantA).participant2(participantC).result(MatchResult.WHITE_WIN).build(),
                MatchEntity.builder().round(round2).participant1(participantE).participant2(participantB).result(MatchResult.WHITE_WIN).build(),
                MatchEntity.builder().round(round2).participant1(participantD).participant2(participantF).result(MatchResult.WHITE_WIN).build()
        ));
        tournament.getRounds().add(round2);

        // Round 3
        // A vs E (A wins) -> A:3, E:2
        // C vs B (C wins) -> C:2, B:0
        // D vs F (D wins) -> D:2, F:0
        RoundEntity round3 = RoundEntity.builder().tournament(tournament).number(3).isFinished(true).build();
        round3.setMatches(List.of(
                MatchEntity.builder().round(round3).participant1(participantA).participant2(participantE).result(MatchResult.WHITE_WIN).build(),
                MatchEntity.builder().round(round3).participant1(participantC).participant2(participantB).result(MatchResult.WHITE_WIN).build(),
                MatchEntity.builder().round(round3).participant1(participantD).participant2(participantF).result(MatchResult.WHITE_WIN).build()
        ));
        tournament.getRounds().add(round3);

        // Add newcomer G before Round 4 (G has score 0.0)
        ParticipantEntity participantG = ParticipantEntity.builder().id(UUID.randomUUID()).nickname("Round4G").initialEloPoints(900).score(BigDecimal.ZERO).build();

        List<ParticipantEntity> participantEntities = List.of(
                participantA, // rank 1 (3.0 pts)
                participantB, // rank 2 (0.0 pts)
                participantC, // rank 3 (2.0 pts)
                participantD, // rank 4 (2.0 pts)
                participantE, // rank 5 (2.0 pts)
                participantF, // rank 6 (0.0 pts)
                participantG  // rank 7 (0.0 pts)
        );

        List<TrfLine> trfLines = TrfService.toTrfTournament(participantEntities, tournament);

        var pairings4 = new JaVaFoPairingStrategyImpl().makePairings(trfLines);
        org.junit.jupiter.api.Assertions.assertNotNull(pairings4);
        org.junit.jupiter.api.Assertions.assertEquals(4, pairings4.size());
        org.junit.jupiter.api.Assertions.assertEquals(Map.of(4, 1, 5, 3, 2, 7, 6, 0), pairings4);
    }
}
