package com.chessgrinder.chessgrinder.chessengine;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.Player001TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.PointsForResultXxsTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.PointsForResultXxsTrfLine.Results;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.util.TrfUtil;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TournamentTrfResultsCalculatorTest {

    @Test
    void testResultsCalculation_nonDefaultXxs() {
        var trf = List.of(
                xxs(new HashMap<>() {{
                    put(Results.WW, 1.0);
                    put(Results.BW, 1.1);
                    put(Results.WL, 0.0);
                    put(Results.BL, 0.1);
                    put(Results.WD, 0.6);
                    put(Results.BD, 0.4);
                    put(Results.PAB, 0.8);
                    put(Results.FPB, 1.2);
                    put(Results.HPB, 0.5);
                    put(Results.ZPB, 0.2);
                }}),
                player(1, "0 w 1"),
                player(2, "0 b 0"),
                player(3, "4 w 0"),
                player(4, "3 b 1"),
                player(5, "6 w ="),
                player(6, "5 b ="),
                player(7, "0 - U"),
                player(8, "0 - F"),
                player(9, "0 - H"),
                player(10, "0 - Z")
        );
        List<TrfLine> updatedTrf = TrfUtil.clone(trf);
        TournamentTrfResultsCalculator.updateResults(updatedTrf);
        Map<Integer, Float> points = TrfUtil.players(updatedTrf).collect(Collectors.toMap(Player001TrfLine::getStartingRank, Player001TrfLine::getPoints));
        assertEquals(1.0f, points.get(1), 1e-4);
        assertEquals(0.1f, points.get(2), 1e-4);
        assertEquals(0.0f, points.get(3), 1e-4);
        assertEquals(1.1f, points.get(4), 1e-4);
        assertEquals(0.6f, points.get(5), 1e-4);
        assertEquals(0.4f, points.get(6), 1e-4);
        assertEquals(0.8f, points.get(7), 1e-4);
        assertEquals(1.2f, points.get(8), 1e-4);
        assertEquals(0.5f, points.get(9), 1e-4);
        assertEquals(0.2f, points.get(10), 1e-4);
    }

    @Test
    void testResultsCalculation_fallback() {
        var trf = List.of(
                xxs(new HashMap<>() {{
                    put(Results.W, 1.1);
                    put(Results.D, 0.6);
                    put(Results.L, 0.1);
                }}),
                player(1, "0 w 1"),
                player(2, "0 b 0"),
                player(3, "4 w 0"),
                player(4, "3 b 1"),
                player(5, "6 w ="),
                player(6, "5 b ="),
                player(7, "0 - U"),
                player(8, "0 - F"),
                player(9, "0 - H"),
                player(10, "0 - Z")
        );
        List<TrfLine> updatedTrf = TrfUtil.clone(trf);
        TournamentTrfResultsCalculator.updateResults(updatedTrf);
        Map<Integer, Float> points = TrfUtil.players(updatedTrf).collect(Collectors.toMap(Player001TrfLine::getStartingRank, Player001TrfLine::getPoints));
        assertEquals(1.1f, points.get(1), 1e-4);
        assertEquals(0.1f, points.get(2), 1e-4);
        assertEquals(0.1f, points.get(3), 1e-4);
        assertEquals(1.1f, points.get(4), 1e-4);
        assertEquals(0.6f, points.get(5), 1e-4);
        assertEquals(0.6f, points.get(6), 1e-4);
        assertEquals(1.1f, points.get(7), 1e-4);
        assertEquals(1.1f, points.get(8), 1e-4);
        assertEquals(0.6f, points.get(9), 1e-4);
        assertEquals(0.1f, points.get(10), 1e-4);
    }

    @Test
    void testResultsCalculation_sum() {
        var trf = List.of(
                xxs(new HashMap<>() {{
                    put(Results.WW, 123.4);
                    put(Results.BD, 777.7);
                    put(Results.PAB, 34.8);
                }}),
                player(1, "0 w 1", "0 b =", "0 - U")
        );
        List<TrfLine> updatedTrf = TrfUtil.clone(trf);
        TournamentTrfResultsCalculator.updateResults(updatedTrf);
        Map<Integer, Float> points = TrfUtil.players(updatedTrf).collect(Collectors.toMap(Player001TrfLine::getStartingRank, Player001TrfLine::getPoints));
        assertEquals(123.4 + 777.7 + 34.8, points.get(1), 1e-4);
    }

    private static Player001TrfLine player(int playerId, String... matches) {
        Player001TrfLine player = new Player001TrfLine();
        player.setStartingRank(playerId);
        player.setMatches(new ArrayList<>());
        for (String match : matches) {
            player.getMatches().add(new Player001TrfLine.Match(match.charAt(0) - '0', match.charAt(2), match.charAt(4)));
        }
        return player;
    }

    private static PointsForResultXxsTrfLine xxs(Map<Results, Double> pointsForResult) {
        return new PointsForResultXxsTrfLine(pointsForResult);
    }
}
