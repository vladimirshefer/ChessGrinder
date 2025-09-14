package com.chessgrinder.chessgrinder.chessengine;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.Player001TrfLine.TrfMatchResult;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.PointsForResultXxsTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.PointsForResultXxsTrfLine.Results;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.util.TrfUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.chessgrinder.chessgrinder.chessengine.trf.dto.PointsForResultXxsTrfLine.Results.D;
import static com.chessgrinder.chessgrinder.chessengine.trf.dto.PointsForResultXxsTrfLine.Results.L;
import static com.chessgrinder.chessgrinder.chessengine.trf.dto.PointsForResultXxsTrfLine.Results.PAB;
import static com.chessgrinder.chessgrinder.chessengine.trf.dto.PointsForResultXxsTrfLine.Results.W;

public class TournamentTrfResultsCalculator {

    public static void updateResults(List<TrfLine> trf) {
        Map<Results, Double> specifiedResults = TrfUtil.filterByType(trf, PointsForResultXxsTrfLine.class)
                .map(PointsForResultXxsTrfLine::getPointsForResult)
                .flatMap(it -> it.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

        TrfUtil.players(trf).forEach(player -> {
            double pointsSum = player.getMatches().stream()
                    .map(match -> result(TrfMatchResult.fromCode(match.getResult()), match.getColor(), specifiedResults))
                    .mapToDouble(it -> it)
                    .sum();
            player.setPoints((float) pointsSum);
        });
    }

    private static Double result(TrfMatchResult result, char color, Map<Results, Double> xxsResults) {
        if (result == TrfMatchResult.PAIRING_ALLOCATED_BYE) return getResult(xxsResults, PAB);
        if (result == TrfMatchResult.ZERO_POINT_BYE) return getResult(xxsResults, Results.ZPB);
        if (result == TrfMatchResult.FULL_POINT_BYE) return getResult(xxsResults, Results.FPB);
        if (result == TrfMatchResult.HALF_POINT_BYE) return getResult(xxsResults, Results.HPB);

        if (result == TrfMatchResult.WIN && color == 'w') return getResult(xxsResults, Results.WW);
        if (result == TrfMatchResult.WIN && color == 'b') return getResult(xxsResults, Results.BW);
        if (result == TrfMatchResult.LOSS && color == 'w') return getResult(xxsResults, Results.WL);
        if (result == TrfMatchResult.LOSS && color == 'b') return getResult(xxsResults, Results.BL);
        if (result == TrfMatchResult.DRAW && color == 'w') return getResult(xxsResults, Results.WD);
        if (result == TrfMatchResult.DRAW && color == 'b') return getResult(xxsResults, Results.BD);

        if (result == TrfMatchResult.QUICK_WIN && color == 'w') return getResult(xxsResults, Results.WW);
        if (result == TrfMatchResult.QUICK_WIN && color == 'b') return getResult(xxsResults, Results.BW);
        if (result == TrfMatchResult.QUICK_LOSS && color == 'w') return getResult(xxsResults, Results.WL);
        if (result == TrfMatchResult.QUICK_LOSS && color == 'b') return getResult(xxsResults, Results.BL);
        if (result == TrfMatchResult.QUICK_DRAW && color == 'w') return getResult(xxsResults, Results.WD);
        if (result == TrfMatchResult.QUICK_DRAW && color == 'b') return getResult(xxsResults, Results.BD);

        if (result == TrfMatchResult.FORFEIT_WIN) return getResult(xxsResults, Results.FW);
        if (result == TrfMatchResult.FORFEIT_LOSS) return getResult(xxsResults, Results.FL);

        throw new IllegalArgumentException("Invalid result " + result + " for color " + color);
    }

    private static double getResult(Map<Results, Double> results, Results result) {
        if (results.containsKey(result)) return results.get(result);
        var fallback = switch (result) {
            case WW, BW, FPB, PAB, FW, W -> W;
            case WD, HPB, D, BD -> D;
            case WL, BL, ZPB, FL, L -> L;
        };
        if (results.containsKey(fallback)) return results.get(fallback);
        return result.getDefaultValue();
    }
}
