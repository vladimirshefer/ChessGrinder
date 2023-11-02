package com.chessgrinder.chessgrinder.chessengine;

import com.chessgrinder.chessgrinder.dto.MatchDto;
import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import jakarta.annotation.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static com.chessgrinder.chessgrinder.chessengine.SwissMatchupStrategyImpl.split;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(MockitoExtension.class)
public class SwissMatchupStrategyImplTest {

    SwissMatchupStrategyImpl swissEngine = new SwissMatchupStrategyImpl();

    @Test
    public void testSplit() {
        assertEquals(List.of(), split(List.of()));
        assertEquals(List.of(List.of(1)), split(List.of(1)));
        assertEquals(List.of(List.of(1), List.of(2)), split(List.of(1, 2)));
        assertEquals(List.of(List.of(1, 2, 3), List.of(4, 5)), split(List.of(1, 2, 3, 4, 5)));
    }

    @Test
    public void test_4players() {

        ParticipantDto participant1 = createParticipant("user1", 10, 6);
        ParticipantDto participant2 = createParticipant("user2", 8, 6);
        ParticipantDto participant3 = createParticipant("user3", 6, 6);
        ParticipantDto participant4 = createParticipant("user4", 4, 6);

        List<ParticipantDto> participants = List.of(participant1, participant2, participant3, participant4);

        List<MatchDto> matches = swissEngine.matchUp(participants, emptyList(), false);

        assertEquals(2, matches.size());

        assertEquals(participant1, matches.get(0).getWhite());
        assertEquals(participant2, matches.get(0).getBlack());

        assertEquals(participant3, matches.get(1).getWhite());
        assertEquals(participant4, matches.get(1).getBlack());

    }

    @Test
    public void test_5players_with_buy() {
        ParticipantDto participant1 = createParticipant("user1", 10, 6);
        ParticipantDto participant2 = createParticipant("user2", 8, 6);
        ParticipantDto participant3 = createParticipant("user3", 6, 6);
        ParticipantDto participant4 = createParticipant("user4", 4, 6);
        ParticipantDto participant5 = createParticipant("user5", 4, 6);

        List<ParticipantDto> participants = List.of(participant1, participant2, participant3, participant4, participant5);

        List<MatchDto> matches = swissEngine.matchUp(participants, emptyList(), false);
        MatchDto firstMatch = matches.get(0);
        MatchDto secondMatch = matches.get(1);
        MatchDto buy = matches.get(2);

        assertEquals(3, matches.size());

        assertEquals(participant1, firstMatch.getWhite());
        assertEquals(participant2, firstMatch.getBlack());

        assertEquals(participant3, secondMatch.getWhite());
        assertEquals(participant4, secondMatch.getBlack());

        assertEquals(participant5, buy.getWhite());
        assertNull(buy.getBlack());
        assertEquals(MatchResult.BUY, buy.getResult());
    }

    @Test
    public void test_ZeroParticipants() {
        List<ParticipantDto> participants = List.of();
        List<MatchDto> matches = swissEngine.matchUp(participants, null, false);

        assertEquals(0, matches.size());
    }

    @Test
    public void test_NoRepeatableMatches() {
        ParticipantDto participant1 = createParticipant("user1", 10, 6);
        ParticipantDto participant2 = createParticipant("user2", 8, 6);
        ParticipantDto participant3 = createParticipant("user3", 8, 6);
        ParticipantDto participant4 = createParticipant("user4", 8, 6);

        MatchDto match = createMatch(participant1, participant3, MatchResult.WHITE_WIN);
        List<MatchDto> matchHistory = List.of(match);
        List<ParticipantDto> participants = List.of(participant1, participant2, participant3, participant4);

        List<MatchDto> matches = swissEngine.matchUp(participants, matchHistory, false);

        MatchDto firstMatch = matches.get(0);
        MatchDto secondMatch = matches.get(1);

        assertEquals(2, matches.size());

        assertEquals(participant4, firstMatch.getWhite());
        assertEquals(participant1, firstMatch.getBlack());

        assertEquals(participant2, secondMatch.getWhite());
        assertEquals(participant3, secondMatch.getBlack());
    }

    @Test
    public void test_overflownSwissWithBuy() {
        ParticipantDto participant1 = createParticipant("user1", 0, 0);
        ParticipantDto participant2 = createParticipant("user2", 0, 0);
        ParticipantDto participant3 = createParticipant("user3", 0, 0);
        List<MatchDto> round1 = swissEngine.matchUp(
                List.of(participant1, participant2, participant3),
                List.of(),
                false
        );
        assertEquals(
                List.of(
                        createMatch(participant1, participant2, null),
                        createMatch(participant3, null, MatchResult.BUY)
                ),
                round1
        );

    }

    @Test
    void test3p() {
        runTournament(swissEngine, "user1", "user2", "user3", "user4", "user5")
                .thenRound(round -> round
                        .match(participant("user1", 1, 0), participant("user4", 0, 1), MatchResult.WHITE_WIN)
                        .match(participant("user2", 1, 0), participant("user3", 0, 1), MatchResult.WHITE_WIN)
                        .match(participant("user5", 1, 0), null, MatchResult.BUY)
                )
                .show(System.out::println)
                .thenRound(round -> round
                        .match(participant("user1", 2, 2), participant("user5", 1, 2), MatchResult.WHITE_WIN)
                        .match(participant("user3", 1, 1), participant("user2", 1, 1), MatchResult.WHITE_WIN)
                        .match(participant("user4", 1, 2), null, MatchResult.BUY)
                )
                .show(System.out::println);
    }

    private static MockSwissTournamentRunner runTournament(SwissMatchupStrategyImpl swissEngine, String... participants) {
        return new MockSwissTournamentRunner(swissEngine, participants);
    }

    public static ParticipantDto createParticipant(String name, int score, int buchholz) {
        if (name == null) return null;
        return ParticipantDto.builder()
                .id(name)
                .name(name)
                .score(BigDecimal.valueOf(score))
                .buchholz(BigDecimal.valueOf(buchholz))
                .build();
    }

    public static ParticipantDto participant(String name, double score, double buchholz) {
        if (name == null) return null;
        return ParticipantDto.builder()
                .id(name)
                .name(name)
                .score(BigDecimal.valueOf(score))
                .buchholz(BigDecimal.valueOf(buchholz))
                .build();
    }

    public static MatchDto createMatch(@Nullable ParticipantDto white, @Nullable ParticipantDto black, @Nullable MatchResult result) {
        return MatchDto.builder()
                .white(white)
                .black(black)
                .result(result)
                .build();
    }

}

