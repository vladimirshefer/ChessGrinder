package com.chessgrinder.chessgrinder.testutil.trf;

import com.chessgrinder.chessgrinder.chessengine.PairingFailedException;
import com.chessgrinder.chessgrinder.chessengine.TournamentTrfResultsCalculator;
import com.chessgrinder.chessgrinder.chessengine.pairings.PairingStrategy;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.Player001TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.RoundsNumberXxrTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.util.TrfUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class TrfTournamentGenerator {
    public static List<TrfLine> runTournament(
            PairingStrategy pairingStrategy,
            List<TrfLine> trf
    ) {
        return runTournament(pairingStrategy, trf, blackAlwaysWin());
    }

    public static List<TrfLine> runTournament(
            PairingStrategy pairingStrategy,
            List<TrfLine> trf,
            BiFunction<Integer, Integer, Player001TrfLine.TrfMatchResult> resultForWhite
    ) {
        int numberOfRounds1 = TrfUtil.getPlannedRoundsNumber(trf).orElseThrow();
        for (int roundNumber = 0; roundNumber < numberOfRounds1; roundNumber++) {
            try {
                runRound(pairingStrategy, trf, resultForWhite);
            } catch (Exception e) {
                throw new PairingFailedException("Pairing failed for round " + roundNumber + " TRF: \n" + TrfUtil.writeTrfLines(trf), e);
            }
        }

//        new TrfValidatorImpl().validate(trf);
        return trf;
    }

    public static void runRound(
            PairingStrategy pairingStrategy,
            List<TrfLine> trf
    ) {
        runRound(pairingStrategy, trf, blackAlwaysWin());
    }

    @NotNull
    private static BiFunction<Integer, Integer, Player001TrfLine.TrfMatchResult> blackAlwaysWin() {
        return (playerId, opponentPlayerId) -> {
            if (opponentPlayerId != 0 && playerId != 0) {
                return Player001TrfLine.TrfMatchResult.LOSS;
            } else {
                return Player001TrfLine.TrfMatchResult.PAIRING_ALLOCATED_BYE;
            }
        };
    }

    public static void runRound(
            PairingStrategy pairingStrategy,
            List<TrfLine> trf,
            BiFunction<Integer, Integer, Player001TrfLine.TrfMatchResult> resultForWhite
    ) {
        fillGaps(trf);
        TournamentTrfResultsCalculator.updateResults(trf);
        Map<Integer, Integer> pairings;
        pairings = pairingStrategy.makePairings(trf);
        pairings.forEach((playerId, opponentPlayerId) -> {
            Player001TrfLine.TrfMatchResult whiteResult = resultForWhite.apply(playerId, opponentPlayerId);
            Player001TrfLine.TrfMatchResult blackResult = switch (whiteResult) {
                case PAIRING_ALLOCATED_BYE -> Player001TrfLine.TrfMatchResult.PAIRING_ALLOCATED_BYE;
                case FORFEIT_LOSS -> Player001TrfLine.TrfMatchResult.FORFEIT_WIN;
                case FORFEIT_WIN -> Player001TrfLine.TrfMatchResult.FORFEIT_LOSS;
                case QUICK_WIN -> Player001TrfLine.TrfMatchResult.QUICK_LOSS;
                case QUICK_DRAW -> Player001TrfLine.TrfMatchResult.QUICK_DRAW;
                case QUICK_LOSS -> Player001TrfLine.TrfMatchResult.QUICK_WIN;
                case WIN -> Player001TrfLine.TrfMatchResult.LOSS;
                case LOSS -> Player001TrfLine.TrfMatchResult.WIN;
                case ZERO_POINT_BYE -> Player001TrfLine.TrfMatchResult.ZERO_POINT_BYE;
                case DRAW -> Player001TrfLine.TrfMatchResult.DRAW;
                case HALF_POINT_BYE -> Player001TrfLine.TrfMatchResult.HALF_POINT_BYE;
                case FULL_POINT_BYE -> Player001TrfLine.TrfMatchResult.FULL_POINT_BYE;
            };
            if (playerId != 0 && opponentPlayerId != 0) {
                TrfUtil.player(trf, playerId).getMatches().add(new Player001TrfLine.Match(opponentPlayerId, 'w', whiteResult.getCharCode()));
                TrfUtil.player(trf, opponentPlayerId).getMatches().add(new Player001TrfLine.Match(playerId, 'b', blackResult.getCharCode()));
            } else {
                if (playerId != 0) {
                    TrfUtil.player(trf, playerId).getMatches().add(new Player001TrfLine.Match(0, '-', whiteResult.getCharCode()));
                }
                if (opponentPlayerId != 0) {
                    TrfUtil.player(trf, opponentPlayerId).getMatches().add(new Player001TrfLine.Match(0, '-', blackResult.getCharCode()));
                }
            }
        });
        fillGaps(trf);
    }

    public static List<TrfLine> fillGaps(List<TrfLine> trf) {
        int maxRounds = TrfUtil.players(trf).map(it -> it.getMatches().size()).max(Comparator.naturalOrder()).orElse(0);
        TrfUtil.players(trf).forEach(player -> {
            for (int i = 0; i < maxRounds; i++) {
                if (player.getMatches().size() <= i) {
                    player.getMatches().add(new Player001TrfLine.Match(0, '-', Player001TrfLine.TrfMatchResult.ZERO_POINT_BYE.getCharCode()));
                } else {
                    if (player.getMatches().get(i) == null) {
                        player.getMatches().set(i, new Player001TrfLine.Match(0, '-', Player001TrfLine.TrfMatchResult.ZERO_POINT_BYE.getCharCode()));
                    }
                }
            }
        });
        return trf;
    }

    /**
     * Creates a tournament TRF with specified amount of players and rounds planned, with no rounds played
     */
    public static List<TrfLine> initTournament(int numberOfParticipants, int numberOfRounds) {
        List<TrfLine> trfLines = new ArrayList<>();
        {
            trfLines.add(RoundsNumberXxrTrfLine.of(numberOfRounds));
            for (int playerId = 1; playerId <= numberOfParticipants; playerId++) {
                trfLines.add(generatePlayer(playerId));
            }
        }
        return trfLines;
    }

    public static Player001TrfLine generatePlayer(int playerId) {
        return Player001TrfLine.builder()
                .name("Player" + playerId)
                .startingRank(playerId)
                .matches(new ArrayList<>())
                .build();
    }
}
