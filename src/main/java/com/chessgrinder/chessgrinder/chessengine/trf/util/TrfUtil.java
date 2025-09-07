package com.chessgrinder.chessgrinder.chessengine.trf.util;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.Player001TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.parser.UniversalTrfLineParser;

import java.util.List;
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
}
