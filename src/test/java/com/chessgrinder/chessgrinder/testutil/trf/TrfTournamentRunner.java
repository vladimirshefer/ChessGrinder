package com.chessgrinder.chessgrinder.testutil.trf;

import com.chessgrinder.chessgrinder.chessengine.PairingFailedException;
import com.chessgrinder.chessgrinder.chessengine.pairings.PairingStrategy;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.Player001TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.util.TrfUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TrfTournamentRunner {
    public static void verify(PairingStrategy pairingStrategy, List<TrfLine> trf) {
//        TrfValidator.validateAll(trf);

        for (int roundNumber = 0; roundNumber < TrfUtil.players(trf).map(it -> it.getMatches().size()).findAny().orElseThrow(); roundNumber++) {
            List<TrfLine> inputTrf = TrfUtil.trimRounds(trf, roundNumber);
            Map<Integer, Integer> calculatedPairings;
            try {
                calculatedPairings = pairingStrategy.makePairings(inputTrf);
            } catch (Exception e) {
                throw new PairingFailedException("Pairing failed for round - nothing to compare with" + roundNumber, e);
            }
            Map<Integer, Integer> providedPairings = getProvidedPairings(trf, roundNumber);
            try {
                assertEquals(providedPairings, calculatedPairings, "Pairings don't match for round " + roundNumber);
            } catch (Throwable e) {
                try {
                    System.out.println("Fixed pairings:");
                    System.out.println(TrfUtil.writeTrfLines(fixPairings(pairingStrategy, trf)));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                fail("Pairings failed for round " + roundNumber, e);
            }
        }
    }

    public static List<TrfLine> fixPairings(PairingStrategy pairingStrategy, List<TrfLine> originalTrf) {
        List<TrfLine> copy = TrfUtil.clone(originalTrf);
        for (int roundNumber = 0; roundNumber < TrfUtil.getPlannedRoundsNumber(originalTrf).orElseThrow(); roundNumber++) {
            List<TrfLine> inputTrf = trimRounds(copy, roundNumber);
            Map<Integer, Integer> pairings = pairingStrategy.makePairings(inputTrf);
            fixPairings(pairings, copy, roundNumber);
        }
        return copy;
    }

    @NotNull
    private static Map<Integer, Integer> getProvidedPairings(List<TrfLine> trf, int roundNumber) {
        return TrfUtil.players(trf)
                .map(it -> {
                    int playerId = it.getStartingRank();
                    Player001TrfLine.Match match = it.getMatches().get(roundNumber);
                    int opponentPlayerId = match.getOpponentPlayerId();
                    if (match.getColor() == 'w') {
                        return Map.entry(playerId, opponentPlayerId);
                    }
                    if (match.getColor() == 'b') {
                        return Map.entry(opponentPlayerId, playerId);
                    }
                    if ((match.getColor() == '-' || match.getColor() == ' ') && match.getResult() == Player001TrfLine.TrfMatchResult.PAIRING_ALLOCATED_BYE.getCharCode()) {
                        return Map.entry(playerId, 0);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> {
                            if (!Objects.equals(a, b)) {
                                throw new IllegalStateException("Conflicting pairings for round " + roundNumber + ". Players " + a + " and " + b + " have same opponent");
                            }
                            return a;
                        }
                ));
    }

    private static void fixPairings(Map<Integer, Integer> correctPairings, List<TrfLine> trf, int roundNumber) {
        correctPairings.forEach((playerId, opponentPlayerId) -> {
            try {
                if (playerId != 0) {
                    Player001TrfLine.Match match = TrfUtil.player(trf, playerId).getMatches().get(roundNumber);
                    match.setColor('w');
                    match.setOpponentPlayerId(opponentPlayerId);
                }

                if (opponentPlayerId != 0) {
                    Player001TrfLine.Match match = TrfUtil.player(trf, opponentPlayerId).getMatches().get(roundNumber);
                    match.setColor('b');
                    match.setOpponentPlayerId(playerId);
                }
            } catch (Exception e) {
                throw new RuntimeException("Pairings " + correctPairings, e);
            }
        });
    }


    /**
     * Removes "tail" rounds.
     *
     * @param roundsNumber max number of rounds remaining.
     * @return copy with changed data
     */
    public static List<TrfLine> trimRounds(List<TrfLine> trf, int roundsNumber) {
        return trf.stream()
                .map(it -> {
                    if (it instanceof Player001TrfLine p) {
                        return trimRounds(p, roundsNumber);
                    } else {
                        return it;
                    }
                })
                .toList();
    }

    /**
     * Removes "tail" rounds.
     *
     * @param roundsNumber max number of rounds remaining.
     * @return the copy if the data is changed.
     */
    private static Player001TrfLine trimRounds(Player001TrfLine player, int roundsNumber) {
        if (roundsNumber < 0 || roundsNumber >= player.getMatches().size()) {
            return player;
        }

        List<Player001TrfLine.Match> matches = player.getMatches()
                .stream().limit(roundsNumber).toList();

        return new Player001TrfLine(
                player.getStartingRank(),
                player.getRating(),
                player.getSex(),
                player.getTitle(),
                player.getName(),
                player.getPoints(),
                player.getRank(),
                matches
        );
    }
}
