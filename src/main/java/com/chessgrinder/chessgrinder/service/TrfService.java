package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.MissingPlayersXxzTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.Player001TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.RoundsNumberXxrTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoundEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
public class TrfService {

    public static List<TrfLine> toTrfTournament(List<ParticipantEntity> participantEntities, TournamentEntity tournament) {
        List<UUID> playerIds = participantEntities.stream().map(ParticipantEntity::getId).toList();

        List<TrfLine> trfLines = new ArrayList<>();
        // XXR - number of rounds. required for pairing.
        Integer roundsNumber = tournament.getRoundsNumber();
        if (roundsNumber != null && roundsNumber > 0) {
            trfLines.add(RoundsNumberXxrTrfLine.of(roundsNumber));
        }

        List<UUID> missingParticipantUuids = participantEntities.stream()
                .filter(ParticipantEntity::isMissing)
                .map(ParticipantEntity::getId)
                .toList();

        List<Integer> missingPlayersPairingIds = missingParticipantUuids.stream().map(it1 -> playerIds.indexOf(it1) + 1).toList();
        if (!missingPlayersPairingIds.isEmpty()) {
            trfLines.add(MissingPlayersXxzTrfLine.of(missingPlayersPairingIds));
        }

        List<Player001TrfLine> playerTrfLines = participantEntities
                .stream()
                .map(participant -> {
                    int playerId = playerIds.indexOf(participant.getId()) + 1;
                    return Player001TrfLine.builder()
                            .startingRank(playerId)
                            .name(participant.getUser() != null && participant.getUser().getName() != null ? participant.getUser().getName() : participant.getNickname())
                            .rating(participant.getInitialEloPoints() != 0 && participant.getInitialEloPoints() > 0 ? participant.getInitialEloPoints() : null)
                            .points(participant.getScore() != null ? participant.getScore().floatValue() : null)
                            .matches(new ArrayList<>())
                            .build();
                })
                .toList();

        List<RoundEntity> rounds = tournament.getRounds().stream().sorted(Comparator.comparing(RoundEntity::getNumber)).toList();
        for (int roundIndex = 0; roundIndex < rounds.size(); roundIndex++) {
            RoundEntity round = rounds.get(roundIndex);
            int roundNumber = round.getNumber();
            if (roundNumber - 1 != roundIndex) {
                log.warn("Round number and index mismatch. Index: {}, number: {}, number should be: {}", roundIndex, roundNumber, roundIndex + 1);
            }
            if (!round.isFinished()) {
                continue;
            }
            List<MatchEntity> matches = round.getMatches();
            if (matches == null || matches.isEmpty()) {
                continue;
            }
            for (MatchEntity match : matches) {
                if (match == null) {
                    continue;
                }
                if (match.getParticipant1() != null) {
                    int playerPairingId = playerIds.indexOf(match.getParticipant1().getId());
                    Player001TrfLine.Match trfMatch;
                    boolean isWhite = match.getResult() != MatchResult.BUY && match.getResult() != MatchResult.MISS;
                    MatchResult result1 = match.getResult();
                    if (result1 == null) throw new IllegalArgumentException("Match result cannot be null");
                    char result = switch (result1) {
                        case DRAW -> Player001TrfLine.TrfMatchResult.DRAW.getCharCode();
                        case BUY -> Player001TrfLine.TrfMatchResult.PAIRING_ALLOCATED_BYE.getCharCode();
                        case MISS -> Player001TrfLine.TrfMatchResult.ZERO_POINT_BYE.getCharCode();
                        case WHITE_WIN -> Player001TrfLine.TrfMatchResult.WIN.getCharCode();
                        case BLACK_WIN -> Player001TrfLine.TrfMatchResult.LOSS.getCharCode();
                    };
                    int opponentId = match.getParticipant2() != null ? playerIds.indexOf(match.getParticipant2().getId()) + 1 : 0;
                    char color = isWhite ? 'w' : '-';
                    trfMatch = new Player001TrfLine.Match(opponentId, color, result);
                    setOrAdd(playerTrfLines.get(playerPairingId).getMatches(), roundNumber - 1, trfMatch);
                }
                if (match.getParticipant2() != null) {
                    int playerPairingId = playerIds.indexOf(match.getParticipant2().getId());
                    Player001TrfLine.Match trfMatch;
                    boolean isBlack = match.getResult() != MatchResult.BUY && match.getResult() != MatchResult.MISS;
                    MatchResult result1 = match.getResult();
                    if (result1 == null) throw new IllegalArgumentException("Match result cannot be null");
                    char result = switch (result1) {
                        case DRAW -> Player001TrfLine.TrfMatchResult.DRAW.getCharCode();
                        case BUY -> Player001TrfLine.TrfMatchResult.PAIRING_ALLOCATED_BYE.getCharCode();
                        case MISS -> Player001TrfLine.TrfMatchResult.ZERO_POINT_BYE.getCharCode();
                        case WHITE_WIN -> Player001TrfLine.TrfMatchResult.LOSS.getCharCode();
                        case BLACK_WIN -> Player001TrfLine.TrfMatchResult.WIN.getCharCode();
                    };
                    int opponentId = match.getParticipant1() != null ? playerIds.indexOf(match.getParticipant1().getId()) + 1 : 0;
                    char color = isBlack ? 'b' : '-';
                    trfMatch = new Player001TrfLine.Match(opponentId, color, result);
                    setOrAdd(playerTrfLines.get(playerPairingId).getMatches(), roundIndex, trfMatch);
                }
            }
            // Fill the gaps
            for (Player001TrfLine player : playerTrfLines) {
                if (player.getMatches().size() < roundNumber) {
                    for (int i = player.getMatches().size(); i < roundNumber; i++) {
                        setOrAdd(player.getMatches(), roundIndex, new Player001TrfLine.Match(0, '-', Player001TrfLine.TrfMatchResult.ZERO_POINT_BYE.getCharCode()));
                    }
                }
            }
        }

        trfLines.addAll(playerTrfLines);
        return trfLines;
    }

    private static <T> void setOrAdd(List<T> list, int index, T value) {
        while (list.size() <= index) {
            list.add(null);
        }
        list.set(index, value);
    }
}
