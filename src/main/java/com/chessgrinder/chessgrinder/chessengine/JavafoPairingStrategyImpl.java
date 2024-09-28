package com.chessgrinder.chessgrinder.chessengine;

import com.chessgrinder.chessgrinder.dto.MatchDto;
import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.trf.dto.MissingPlayersTrfLine;
import com.chessgrinder.chessgrinder.trf.dto.PlayerTrfLineDto;
import com.chessgrinder.chessgrinder.trf.dto.PlayerTrfLineDto.TrfMatchResult;
import com.chessgrinder.chessgrinder.trf.line.MissingPlayersTrfLineParser;
import com.chessgrinder.chessgrinder.trf.line.PlayerTrfLineParser;
import javafo.api.JaVaFoApi;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.function.Consumer;

import static com.chessgrinder.chessgrinder.comparator.ParticipantDtoComparators.COMPARE_PARTICIPANT_DTO_BY_NICKNAME_NULLS_LAST;

@Component
public class JavafoPairingStrategyImpl implements PairingStrategy {
    /**
     * JaVaFo is no thread-safe library, therefore to avoid concurrency problems,
     * the requests to JaVaFo are synchronized on this monitor.
     */
    private static final Object JAVAFO_MONITOR = new Object();

    private final PlayerTrfLineParser playerTrfLineParser = new PlayerTrfLineParser();
    private final MissingPlayersTrfLineParser missingPlayersTrfLineParser = new MissingPlayersTrfLineParser();
    private static final int DEFAULT_RATING = 1000;
    private static final String NEWLINE_REGEX = "\\r?\\n|\\r";
    private static final int STANDARD_PAIRING_CODE = 1000;

    @Override
    public List<MatchDto> makePairings(List<ParticipantDto> participants, List<List<MatchDto>> matchHistory, Integer roundsNumber, boolean recalculateResults) {
        if (participants.isEmpty()) return Collections.emptyList();

        Map<ParticipantDto, List<MatchDto>> participantsMatches = getParticipantsMatches(participants, matchHistory);
        List<String> playerIds = participantsMatches.keySet().stream()
                .sorted(COMPARE_PARTICIPANT_DTO_BY_NICKNAME_NULLS_LAST)
                .map(ParticipantDto::getId).toList();

        StringBuilder stringBuilder = new StringBuilder();
        // XXR - number of rounds. required for pairing.
        stringBuilder.append(String.format("XXR %d", roundsNumber));
        stringBuilder.append("\n");
        Consumer<String> trfCollector = stringBuilder::append;

        List<ParticipantDto> missingParticipants = participants.stream().filter(ParticipantDto::getIsMissing)
                .toList();

        missingPlayersTrfLineParser.tryWrite(trfCollector, MissingPlayersTrfLine.builder()
                .playerIds(missingParticipants.stream().map(it -> playerIds.indexOf(it.getId()) + 1).toList())
                .build()
        );
        stringBuilder.append("\n");
        participantsMatches.forEach((participant, matches) -> {
            PlayerTrfLineDto playerTrfLineDto = toTrfDto(participant, matches, playerIds);
            playerTrfLineParser.tryWrite(trfCollector, playerTrfLineDto);
            trfCollector.accept("\n");
        });

        String pairingsFileContent;
        synchronized (JAVAFO_MONITOR) {
            try {
                pairingsFileContent = JaVaFoApi.exec(STANDARD_PAIRING_CODE, new ByteArrayInputStream(stringBuilder.toString().getBytes()));
            } catch (Exception e) {
                throw new RuntimeException("Could not do pairing via javafo. \n" + stringBuilder, e);
            }
        }

        List<MatchDto> result = Arrays.stream(pairingsFileContent.split(NEWLINE_REGEX))
                .skip(1)
                .map(it -> it.split(" "))
                .map(it ->
                        {
                            ParticipantDto white = getParticipantDto(participants, playerIds, it[0]);
                            ParticipantDto black = getParticipantDto(participants, playerIds, it[1]);
                            return MatchDto.builder()
                                    .white(white)
                                    .black(black)
                                    .result(white == null || black == null ? MatchResult.BUY : null)
                                    .build();
                        }
                )
                .toList();

        List<MatchDto> missingMatches = missingParticipants.stream()
                .map(it -> MatchDto.builder()
                        .white(it)
                        .black(null)
                        .result(MatchResult.MISS)
                        .build()
                )
                .toList();

        var resultWithMisses = new ArrayList<>(result);
        resultWithMisses.addAll(missingMatches);

        return resultWithMisses;
    }

    private static ParticipantDto getParticipantDto(List<ParticipantDto> participants, List<String> playerIds, String playerId) {
        Integer playerIdInt = Integer.valueOf(playerId);
        if (playerIdInt == 0) {
            return null;
        }
        String participantId = playerIds.get(playerIdInt - 1);
        return participants.stream().filter(it -> it.getId().equals(participantId))
                .findAny()
                .get();
    }

    private static PlayerTrfLineDto toTrfDto(
            ParticipantDto participant,
            List<MatchDto> matches,
            List<String> playerIds
    ) {
        int playerId = playerIds.indexOf(participant.getId()) + 1;
        PlayerTrfLineDto playerTrfLineDto = PlayerTrfLineDto.builder()
                .startingRank(playerId)
                .name(participant.getUserFullName() != null ? participant.getUserFullName() : participant.getName())
                .rating(participant.getInitialElo() > 0 ? participant.getInitialElo() : DEFAULT_RATING)
                .points(participant.getScore().floatValue())
                .matches(matches.stream()
                        .map(match -> {
                            if (match == null) {
                                return PlayerTrfLineDto.Match.builder()
                                        .opponentPlayerId(0)
                                        .result(TrfMatchResult.ZERO_POINT_BYE.getCharCode())
                                        .color('-')
                                        .build();
                            }
                            boolean isWhite = match.getWhite().getId().equals(participant.getId());
                            ParticipantDto opponent = isWhite
                                    ? match.getBlack() : match.getWhite();
                            char result = getResultChar(isWhite, match.getResult());

                            return PlayerTrfLineDto.Match.builder()
                                    .opponentPlayerId(opponent != null ? playerIds.indexOf(opponent.getId()) + 1 : 0)
                                    .result(result)
                                    .color(isWhite ? 'w' : 'b')
                                    .build();
                        })
                        .toList()
                )
                .build();
        return playerTrfLineDto;
    }

    public static char getResultChar(boolean isWhite, MatchResult result) {
        if (result.equals(MatchResult.DRAW)) return TrfMatchResult.DRAW.getCharCode();
        if (result.equals(MatchResult.BUY)) return TrfMatchResult.FULL_POINT_BYE.getCharCode();
        if (result.equals(MatchResult.MISS)) return TrfMatchResult.ZERO_POINT_BYE.getCharCode();
        if (isWhite && result.equals(MatchResult.WHITE_WIN)) return TrfMatchResult.WIN.getCharCode();
        if (isWhite && result.equals(MatchResult.BLACK_WIN)) return TrfMatchResult.LOSS.getCharCode();
        if (!isWhite && result.equals(MatchResult.BLACK_WIN)) return TrfMatchResult.WIN.getCharCode();
        if (!isWhite && result.equals(MatchResult.WHITE_WIN)) return TrfMatchResult.LOSS.getCharCode();
        throw new IllegalStateException("Could not decide the match result");
    }

    private static Map<ParticipantDto, List<MatchDto>> getParticipantsMatches(
            List<ParticipantDto> participants,
            List<List<MatchDto>> matchHistory
    ) {
        Map<ParticipantDto, List<MatchDto>> collectForWhites = new HashMap<>();
        for (ParticipantDto participant : participants) {
            collectForWhites.putIfAbsent(participant, new ArrayList<>());
            for (List<MatchDto> matchDtos : matchHistory) {
                MatchDto matchForRound = matchDtos.stream().filter(it ->
                        it.getWhite() != null && it.getWhite().getId().equals(participant.getId()) ||
                                it.getBlack() != null && it.getBlack().getId().equals(participant.getId())
                ).findAny().orElse(null);
                collectForWhites.get(participant).add(matchForRound);
            }
        }
        return collectForWhites;
    }
}
