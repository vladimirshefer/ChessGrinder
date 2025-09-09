package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.util.TrfUtil;
import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoundEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals("""
                """, TrfUtil.writeTrfLines(trfTournament));
    }

}
