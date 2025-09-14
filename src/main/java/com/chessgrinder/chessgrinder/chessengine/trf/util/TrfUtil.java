package com.chessgrinder.chessgrinder.chessengine.trf.util;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.Player001TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.RoundsNumberXxrTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.parser.UniversalTrfLineParser;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TrfUtil {

    public static String writeTrfLines(List<? extends TrfLine> trfLines) {
        return new UniversalTrfLineParser().writeAll(trfLines);
    }

    public static List<TrfLine> readTrf(String trf) {
        return new UniversalTrfLineParser().parseAll(trf);
    }

    public static List<TrfLine> clone(List<? extends TrfLine> trf) {
        return readTrf(writeTrfLines(trf));
    }

    public static Optional<Integer> getPlannedRoundsNumber(List<TrfLine> trf) {
        return trf.stream()
                .filter(it -> it instanceof RoundsNumberXxrTrfLine)
                .map(it -> (RoundsNumberXxrTrfLine) it)
                .findFirst()
                .map(RoundsNumberXxrTrfLine::getRoundsNumber);
    }

    public static Stream<Player001TrfLine> players(List<TrfLine> trf) {
        return filterByType(trf, Player001TrfLine.class);
    }

    public static Player001TrfLine player(List<TrfLine> trf, int playerStartingRank) {
        return players(trf)
                .filter(player -> player.getStartingRank() == playerStartingRank)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player with starting rank " + playerStartingRank + " not found. Players " + players(trf).map(Player001TrfLine::getStartingRank).toList() + " found."));
    }


    public static <T extends TrfLine>Stream<T> filterByType(List<TrfLine>trf , Class<T> clazz) {
        //noinspection unchecked
        return trf.stream()
                .filter(clazz::isInstance)
                .map(it -> (T) it);
    }

    public static List<TrfLine> trimRounds(List<TrfLine> trf, int roundNumber) {
        List<TrfLine> result = TrfUtil.clone(trf);
        TrfUtil.players(result).forEach(player -> {
            player.setMatches(player.getMatches().stream().limit(roundNumber).toList());
        });
        return result;
    }

    /**
     * @param roundNumber 1-based
     * @return Map[white_id : black_id]
     */
    public static Map<Integer, Integer> getPairings(List<TrfLine> trf, int roundNumber) {
        return players(trf)
                .map(player -> {
                    Player001TrfLine.Match match = player.getMatches().get(roundNumber - 1);
                    if (match.getColor() == 'w') {
                        return new SimpleEntry<>(player.getStartingRank(), match.getOpponentPlayerId());
                    } else {
                        return new SimpleEntry<>(match.getOpponentPlayerId(), player.getStartingRank());
                    }
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> {
                            if (Objects.equals(a, b)) return a;
                            else throw new RuntimeException("Duplicate pairings " + a + " and " + b);
                        }
                ));
    }
}
