package com.chessgrinder.chessgrinder.chessengine;

import com.chessgrinder.chessgrinder.dto.MatchDto;
import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import jakarta.annotation.Nullable;
import org.opentest4j.AssertionFailedError;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class MockSwissTournamentRunner {
    private static final double NO_VALUE = -1000;

    private final SwissMatchupStrategyImpl swissEngine;
    private List<ParticipantDto> participants;
    private final List<List<MatchDto>> rounds = new ArrayList<>();

    public MockSwissTournamentRunner(SwissMatchupStrategyImpl swissEngine, String... participants) {
        this.swissEngine = swissEngine;
        this.participants = Arrays.asList(participants).stream().map(it -> it != null ? SwissMatchupStrategyImplTest.participant(it, 0, 0) : null).toList();
    }

    public MockSwissTournamentRunner thenRound(Consumer<MockRoundBuilder> round) {
        int newRoundNumber = rounds.size();
        List<MatchDto> actualMatches = swissEngine.matchUp(
                participants,
                rounds.stream().flatMap(Collection::stream).collect(Collectors.toList()),
                true
        );
        MockRoundBuilder mockRoundBuilder = new MockRoundBuilder();
        round.accept(mockRoundBuilder);
        List<MatchDto> expectedResults = mockRoundBuilder.getExpectedResults();
        var matchingMatches = new HashMap<MatchDto, MatchDto>();
        for (MatchDto expectedResult : expectedResults) {
            MatchDto actualMatch = actualMatches.stream().filter(actualPairing -> {
                        var expectedBlackId = Optional.ofNullable(expectedResult.getBlack()).map(ParticipantDto::getId).orElse(null);
                        var actualBlackId = Optional.ofNullable(actualPairing.getBlack()).map(ParticipantDto::getId).orElse(null);
                        var expectedWhiteId = Optional.ofNullable(expectedResult.getWhite()).map(ParticipantDto::getId).orElse(null);
                        var actualWhiteId = Optional.ofNullable(actualPairing.getWhite()).map(ParticipantDto::getId).orElse(null);
                        return Objects.equals(expectedWhiteId, actualWhiteId) && Objects.equals(expectedBlackId, actualBlackId);
                    })
                    .findAny()
                    .orElseThrow(() ->
                            new AssertionFailedError(
                                    "Expected match not found in round " + newRoundNumber + " " +
                                            "\n" + "Expected match: " + asString(expectedResult) +
                                            "\n" + "Actual matches: \n" + actualMatches.stream().map(matchDto -> asString(matchDto)).collect(Collectors.joining("\n"))
                            )
                    );
            matchingMatches.put(expectedResult, actualMatch);
            actualMatch.setResult(expectedResult.getResult());
        }

        rounds.add(actualMatches);

        {
            TournamentResultsCalculator tournamentResultsCalculator = new TournamentResultsCalculator(participants, rounds.stream().flatMap(Collection::stream).toList());
            tournamentResultsCalculator.calculate();
            participants = tournamentResultsCalculator.getResult();
        }


        // Validate score and bhz if specified
        for (MatchDto expectedResult : expectedResults) {
            MatchDto actualMatch = matchingMatches.get(expectedResult);
            {
                ParticipantDto p = expectedResult.getWhite();
                if (p != null) {
                    double expectedScore = p.getScore().doubleValue();
                    double actualScore = actualMatch.getWhite().getScore().doubleValue();
                    if (expectedScore != NO_VALUE && expectedScore != actualScore) {
                        throw new AssertionFailedError(
                                "Participant " + p.getId() + " should have score " + expectedScore +
                                        ", but has " + actualScore +
                                        " in round " + newRoundNumber + "!"
                        );
                    }
                }
            }
            {
                ParticipantDto p = expectedResult.getWhite();
                if (p != null) {
                    double expectedBuchholz = p.getBuchholz().doubleValue();
                    double actualBuchholz = actualMatch.getWhite().getBuchholz().doubleValue();
                    if (expectedBuchholz != NO_VALUE && expectedBuchholz != actualBuchholz) {
                        throw new AssertionFailedError(
                                "Participant " + p.getId() + " should have buchholz " + expectedBuchholz +
                                        ", but has " + actualBuchholz +
                                        " in round " + newRoundNumber + "!"
                        );
                    }
                }
            }
            {
                ParticipantDto p = expectedResult.getBlack();
                if (p != null) {
                    double expectedScore = p.getScore().doubleValue();
                    double actualScore = actualMatch.getBlack().getScore().doubleValue();
                    if (expectedScore != NO_VALUE && expectedScore != actualScore) {
                        throw new AssertionFailedError(
                                "Participant " + p.getId() + " should have score " + expectedScore +
                                        ", but has " + actualScore +
                                        " in round " + newRoundNumber + "!"
                        );
                    }
                }
            }
            {
                ParticipantDto black = expectedResult.getBlack();
                if (black != null) {
                    double expectedBuchholz = black.getBuchholz().doubleValue();
                    double actualBuchholz = actualMatch.getBlack().getBuchholz().doubleValue();
                    if (expectedBuchholz != NO_VALUE && expectedBuchholz != actualBuchholz) {
                        throw new AssertionFailedError(
                                "Participant " + black.getId() + " should have buchholz " + expectedBuchholz +
                                        ", but has " + actualBuchholz +
                                        " in round " + newRoundNumber + "!"
                        );
                    }
                }
            }
        }

        return this;
    }

    private static String asString(MatchDto matchDto) {
        if (matchDto == null) return "null";
        return "match(" +
                (matchDto.getWhite() != null ? matchDto.getWhite().getId() : null) +
                ", " +
                (matchDto.getBlack() != null ? matchDto.getBlack().getId() : null) +
                ", " +
                matchDto.getResult() +
                ")";
    }

    private static String asStringWithResults(MatchDto matchDto) {
        if (matchDto == null) return "null";
        return "match(" +
                (matchDto.getWhite() != null ? (
                        matchDto.getWhite().getId() + "(" + matchDto.getWhite().getScore() + ", " + matchDto.getWhite().getBuchholz() + ")"
                ) : null) +
                ", " +
                (matchDto.getBlack() != null ? (
                        matchDto.getBlack().getId() + "(" + matchDto.getBlack().getScore() + ", " + matchDto.getBlack().getBuchholz() + ")"
                ) : null) +
                ", " +
                matchDto.getResult() +
                ")";
    }

    public MockSwissTournamentRunner show(Consumer<String> printer) {
        int lastRoundIndex = rounds.size() - 1;
        List<MatchDto> round = rounds.get(lastRoundIndex);
        printer.accept("Round " + lastRoundIndex);
        for (MatchDto matchDto : round) {
            printer.accept(asStringWithResults(matchDto));
        }
        return this;
    }

    public static class MockRoundBuilder {

        private final List<MatchDto> matches = new ArrayList<>();

        public MockRoundBuilder() {
        }

        public MockRoundBuilder match(@Nullable String user1, @Nullable String user2, @Nullable MatchResult matchResult) {
            matches.add(SwissMatchupStrategyImplTest.createMatch(
                    SwissMatchupStrategyImplTest.participant(user1, NO_VALUE, NO_VALUE),
                    SwissMatchupStrategyImplTest.participant(user2, NO_VALUE, NO_VALUE),
                    matchResult
            ));
            return this;
        }

        private List<MatchDto> getExpectedResults() {
            return matches;
        }

        public MockRoundBuilder match(ParticipantDto user1, ParticipantDto user2, MatchResult matchResult) {
            matches.add(SwissMatchupStrategyImplTest.createMatch(
                    user1,
                    user2,
                    matchResult
            ));
            return this;

        }
    }

}
