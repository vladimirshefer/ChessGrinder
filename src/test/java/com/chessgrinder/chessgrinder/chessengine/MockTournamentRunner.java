package com.chessgrinder.chessgrinder.chessengine;

import com.chessgrinder.chessgrinder.dto.MatchDto;
import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import jakarta.annotation.Nullable;
import org.opentest4j.AssertionFailedError;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class MockTournamentRunner {
    private static final double NO_VALUE = -1000;

    private final PairingStrategy pairingStrategy;
    private List<ParticipantDto> participants;
    private final List<List<MatchDto>> rounds = new ArrayList<>();

    public MockTournamentRunner(PairingStrategy pairingStrategy, String... participants) {
        this.pairingStrategy = pairingStrategy;
        this.participants = new ArrayList<>(Arrays.stream(participants).map(it -> it != null ? MockTournamentRunnerUtils.participant(it, 0, 0) : null).toList());
    }

    public MockTournamentRunner thenRound(Consumer<MockRoundBuilder> round) {
        int newRoundNumber = rounds.size();
        List<MatchDto> actualMatches = pairingStrategy.makePairings(
                participants,
                rounds,
                1000,
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
        if (expectedResults.size() != actualMatches.size()) {
            throw new AssertionError("Different pairings size " + expectedResults.size() + " " + actualMatches.size());
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
        return ".match(\"" +
                (matchDto.getWhite() != null ? matchDto.getWhite().getId() : null) +
                "\", \"" +
                (matchDto.getBlack() != null ? matchDto.getBlack().getId() : null) +
                "\", " +
                matchDto.getResult() +
                ")";
    }

    private static String asStringWithResults(MatchDto matchDto) {
        if (matchDto == null) return "null";
        return "match(" +
                (matchDto.getWhite() != null ? asStringWithResults(matchDto.getWhite()) : null) +
                ", " +
                (matchDto.getBlack() != null ? asStringWithResults(matchDto.getBlack()) : null) +
                ", " +
                matchDto.getResult() +
                ")";
    }

    private static String asStringWithResults(ParticipantDto participant) {
        return participant.getId() + "(" + participant.getScore() + ", " + participant.getBuchholz() + ")";
    }

    public MockTournamentRunner show(Consumer<String> printer) {
        int lastRoundIndex = rounds.size() - 1;
        List<MatchDto> round = rounds.get(lastRoundIndex);
        printer.accept("Round " + lastRoundIndex);
        for (MatchDto matchDto : round) {
            printer.accept(asStringWithResults(matchDto));
        }
        return this;
    }

    public MockTournamentRunner showParticipants(Consumer<String> printer) {
        List<ParticipantDto> sorted = participants.stream()
                .sorted(Comparator
                        .comparing(ParticipantDto::getScore)
                        .thenComparing(ParticipantDto::getBuchholz)
                        .reversed()
                )
                .toList();
        for (ParticipantDto participant : sorted) {
            printer.accept(asStringWithResults(participant));
        }
        return this;
    }

    public MockTournamentRunner newParticipant(String name) {
        participants.add(MockTournamentRunnerUtils.participant(name, 0, 0));
        return this;
    }

    public MockTournamentRunner missParticipant(String name) {
        participants.stream().filter(it -> it.getId().equals(name))
                .findAny().orElseThrow()
                .setIsMissing(true);
        return this;
    }

    public MockTournamentRunner returnParticipant(String name) {
        participants.stream().filter(it -> it.getId().equals(name))
                .findAny().orElseThrow()
                .setIsMissing(false);
        return this;
    }

    public static class MockRoundBuilder {

        private final List<MatchDto> matches = new ArrayList<>();

        public MockRoundBuilder() {
        }

        public MockRoundBuilder match(@Nullable String user1, @Nullable String user2, @Nullable MatchResult matchResult) {
            matches.add(MockTournamentRunnerUtils.createMatch(
                    MockTournamentRunnerUtils.participant(user1, NO_VALUE, NO_VALUE),
                    MockTournamentRunnerUtils.participant(user2, NO_VALUE, NO_VALUE),
                    matchResult
            ));
            return this;
        }

        private List<MatchDto> getExpectedResults() {
            return matches;
        }

        /**
         * Asserts that this round should have these players to be paired and
         * if they are paired sets the result for the next pairing.
         * @param user1 player which should be paired as white
         * @param user2 player which should be paired as black
         * @param matchResult if pairing exists, this is the result for next round pairing.
         * @return self
         */
        public MockRoundBuilder match(ParticipantDto user1, ParticipantDto user2, MatchResult matchResult) {
            matches.add(MockTournamentRunnerUtils.createMatch(
                    user1,
                    user2,
                    matchResult
            ));
            return this;

        }
    }

}
