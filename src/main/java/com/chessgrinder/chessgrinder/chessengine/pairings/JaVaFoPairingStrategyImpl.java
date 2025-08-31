package com.chessgrinder.chessgrinder.chessengine.pairings;

import com.chessgrinder.chessgrinder.dto.MatchDto;
import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.trf.TrfUtil;
import com.chessgrinder.chessgrinder.trf.dto.MissingPlayersXxzTrfLine;
import com.chessgrinder.chessgrinder.trf.dto.Player001TrfLine;
import com.chessgrinder.chessgrinder.trf.dto.Player001TrfLine.TrfMatchResult;
import com.chessgrinder.chessgrinder.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.trf.dto.RoundsNumberXxrTrfLine;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.chessgrinder.chessgrinder.comparator.ParticipantDtoComparators.COMPARE_PARTICIPANT_DTO_BY_NICKNAME_NULLS_LAST;

@Component
public class JaVaFoPairingStrategyImpl implements PairingStrategy {

    private static final int DEFAULT_RATING = 1000;
    private static final String NEWLINE_REGEX = "\\r?\\n|\\r";

    @Override
    @WithSpan
    public List<MatchDto> makePairings(List<ParticipantDto> participants, List<List<MatchDto>> matchHistory, Integer roundsNumber) {
        if (participants.isEmpty()) return Collections.emptyList();

        Map<ParticipantDto, List<MatchDto>> participantsMatches = getParticipantsMatches(participants, matchHistory);
        List<String> playerIds = participantsMatches.keySet().stream()
                .sorted(COMPARE_PARTICIPANT_DTO_BY_NICKNAME_NULLS_LAST)
                .map(ParticipantDto::getId).toList();

        List<TrfLine> trfLines = new ArrayList<>();
        // XXR - number of rounds. required for pairing.
        trfLines.add(RoundsNumberXxrTrfLine.builder().roundsNumber(roundsNumber).build());

        List<ParticipantDto> missingParticipants = participants.stream().filter(ParticipantDto::getIsMissing).toList();
        trfLines.add(
                MissingPlayersXxzTrfLine.builder()
                        .playerIds(missingParticipants.stream().map(it1 -> playerIds.indexOf(it1.getId()) + 1).toList())
                        .build()
        );

        List<Player001TrfLine> playerTrfLines = participantsMatches.entrySet()
                .stream()
                .map(entry -> toTrfDto(entry.getKey(), entry.getValue(), playerIds))
                .toList();

        trfLines.addAll(playerTrfLines);

        String tournamentTrf = TrfUtil.writeTrfLines(trfLines);
        String pairingsFileContent;
        try {
            pairingsFileContent = JaVaFoWrapper.exec(JaVaFoWrapper.ExecutionCodes.PAIRING, tournamentTrf);
        } catch (Exception e) {
            throw new RuntimeException("Could not do pairing via javafo. \n" + tournamentTrf, e);
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

        List<MatchDto> missingMatches = participants
                .stream()
                .filter(ParticipantDto::getIsMissing)
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

    public static Player001TrfLine toTrfDto(
            ParticipantDto participant,
            List<MatchDto> matches,
            List<String> playerIds
    ) {
        int playerId = playerIds.indexOf(participant.getId()) + 1;
        Player001TrfLine playerTrfLineDto = Player001TrfLine.builder()
                .startingRank(playerId)
                .name(participant.getUserFullName() != null ? participant.getUserFullName() : participant.getName())
                .rating(participant.getInitialElo() != null && participant.getInitialElo() > 0 ? participant.getInitialElo() : DEFAULT_RATING)
                .points(participant.getScore().floatValue())
                .matches(matches.stream()
                        .map(match -> {
                            if (match == null) {
                                return Player001TrfLine.Match.builder()
                                        .opponentPlayerId(0)
                                        .result(TrfMatchResult.ZERO_POINT_BYE.getCharCode())
                                        .color('-')
                                        .build();
                            }
                            boolean isWhite = false;
                            if (match.getWhite() != null) {
                                isWhite = match.getWhite().getId().equals(participant.getId());
                            }
                            ParticipantDto opponent = isWhite
                                    ? match.getBlack() : match.getWhite();
                            char result = getResultChar(isWhite, match.getResult());

                            return Player001TrfLine.Match.builder()
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
        if (result.equals(MatchResult.BUY)) return TrfMatchResult.PAIRING_ALLOCATED_BYE.getCharCode();
        if (result.equals(MatchResult.MISS)) return TrfMatchResult.ZERO_POINT_BYE.getCharCode();
        if (isWhite && result.equals(MatchResult.WHITE_WIN)) return TrfMatchResult.WIN.getCharCode();
        if (isWhite && result.equals(MatchResult.BLACK_WIN)) return TrfMatchResult.LOSS.getCharCode();
        if (!isWhite && result.equals(MatchResult.BLACK_WIN)) return TrfMatchResult.WIN.getCharCode();
        if (!isWhite && result.equals(MatchResult.WHITE_WIN)) return TrfMatchResult.LOSS.getCharCode();
        throw new IllegalStateException("Could not decide the match result");
    }

    public static Map<ParticipantDto, List<MatchDto>> getParticipantsMatches(
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
