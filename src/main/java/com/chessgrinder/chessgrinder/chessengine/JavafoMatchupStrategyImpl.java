package com.chessgrinder.chessgrinder.chessengine;

import com.chessgrinder.chessgrinder.dto.MatchDto;
import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.trf.dto.PlayerTrfLineDto;
import com.chessgrinder.chessgrinder.trf.line.PlayerTrfLineParser;
import javafo.api.JaVaFoApi;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class JavafoMatchupStrategyImpl implements MatchupStrategy {
    private final PlayerTrfLineParser playerTrfLineParser = new PlayerTrfLineParser();

    @Override
    public List<MatchDto> matchUp(List<ParticipantDto> participants, List<MatchDto> matchHistory, boolean recalculateResults) {
        HashMap<ParticipantDto, List<MatchDto>> participantsMatches = getParticipantsMatches(matchHistory);
        for (ParticipantDto participant : participants) {
            participantsMatches.putIfAbsent(participant, new ArrayList<>());
        }
        List<String> playerIds = participantsMatches.keySet().stream()
                .sorted(Comparator.comparing(ParticipantDto::getName))
                .map(ParticipantDto::getId).toList();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("XXR 1000");
        stringBuilder.append("\n");
        Consumer<String> trfCollector = (it) -> stringBuilder.append(it);

        participantsMatches.forEach((participant, matches) -> {
            PlayerTrfLineDto playerTrfLineDto = getPlayerTrfLineDto(participant, matches, playerIds);
            playerTrfLineParser.tryWrite(trfCollector, playerTrfLineDto);
            trfCollector.accept("\n");
        });

//        System.out.println(stringBuilder);

        String pairingsFileContent = JaVaFoApi.exec(1000, new ByteArrayInputStream(stringBuilder.toString().getBytes()));
//        System.out.println(pairingsFileContent);

        List<MatchDto> result = Arrays.stream(pairingsFileContent.split("\n"))
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

        return result;
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

    private static PlayerTrfLineDto getPlayerTrfLineDto(
            ParticipantDto participant,
            List<MatchDto> matches,
            List<String> playerIds
    ) {
        int playerId = playerIds.indexOf(participant.getId()) + 1;
        PlayerTrfLineDto playerTrfLineDto = PlayerTrfLineDto.builder()
                .startingRank(playerId)
                .name(participant.getUserFullName() != null ? participant.getUserFullName() : participant.getName())
                .rating(1000)
                .points(participant.getScore().floatValue())
                .matches(matches.stream()
                        .map(match -> {
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
        if (result.equals(MatchResult.DRAW)) return PlayerTrfLineDto.MatchResult.DRAW.getCharCode();
        if (result.equals(MatchResult.BUY)) return PlayerTrfLineDto.MatchResult.FULL_POINT_BYE.getCharCode();
        if (isWhite && result.equals(MatchResult.WHITE_WIN)) return PlayerTrfLineDto.MatchResult.WIN.getCharCode();
        if (isWhite && result.equals(MatchResult.BLACK_WIN)) return PlayerTrfLineDto.MatchResult.LOSS.getCharCode();
        if (!isWhite && result.equals(MatchResult.BLACK_WIN)) return PlayerTrfLineDto.MatchResult.WIN.getCharCode();
        if (!isWhite && result.equals(MatchResult.WHITE_WIN)) return PlayerTrfLineDto.MatchResult.LOSS.getCharCode();
        throw new IllegalStateException("Could not decide the match result");
    }

    private static HashMap<ParticipantDto, List<MatchDto>> getParticipantsMatches(List<MatchDto> matchHistory) {
        Map<ParticipantDto, List<MatchDto>> collectForWhites = matchHistory.stream()
                .filter(it -> it.getWhite() != null)
                .collect(Collectors.toMap(
                        MatchDto::getWhite,
                        Arrays::asList,
                        (a, b) -> concatLists(a, b)
                ));
        Map<ParticipantDto, List<MatchDto>> collectForBlacks = matchHistory.stream()
                .filter(it -> it.getBlack() != null)
                .collect(Collectors.toMap(
                        MatchDto::getBlack,
                        Arrays::asList,
                        (a, b) -> concatLists(a, b)
                ));
        HashMap<ParticipantDto, List<MatchDto>> collect = new HashMap<>(collectForWhites);
        collect.putAll(collectForBlacks);
        return collect;
    }

    private static <T> List<T> concatLists(List<T> a, List<T> b) {
        ArrayList<T> result = new ArrayList<>(a);
        result.addAll(b);
        return result;
    }
}
