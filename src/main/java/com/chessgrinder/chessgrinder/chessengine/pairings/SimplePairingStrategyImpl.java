package com.chessgrinder.chessgrinder.chessengine.pairings;

import com.chessgrinder.chessgrinder.chessengine.PairingFailedException;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.MissingPlayersXxzTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.Player001TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.util.TrfUtil;
import jakarta.annotation.Nullable;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A fallback pairing system with minimal guarantees and no strict quality constraints.
 * <p>
 * Unlike standard systems such as Swiss or round-robin, which may refuse to generate pairings
 * if their requirements cannot be satisfied, this implementation always produces some result -
 * even for highly irregular tournaments. Its goal is to ensure that pairings exist under any
 * circumstances.
 * </p>
 *
 * <p><b>Guaranteed properties (minimal requirements):</b></p>
 * <ul>
 *   <li>Every registered player always appears in the results.</li>
 *   <li>No "phantom" players are created (only actual participants are used).</li>
 *   <li>A player cannot be paired with multiple opponents in the same round.</li>
 *   <li>This implementation is <b>stable</b> and <b>idempotent</b>: for the same tournament state
 *   it will always produce the same result, without relying on randomness.</li>
 * </ul>
 *
 * <p><b>Soft goals (best-effort, not guaranteed):</b></p>
 * <ul>
 *   <li>Avoid repeated pairings between the same players.</li>
 *   <li>Balance color allocation (black/white) across rounds.</li>
 *   <li>Minimize byes and avoid giving multiple byes to the same player unless strictly necessary.</li>
 * </ul>
 */
public class SimplePairingStrategyImpl implements PairingStrategy {
    @Override
    public Map<Integer, Integer> makePairings(List<TrfLine> trf) {
        Set<Integer> missingPLayerIds = trf.stream().filter(it -> it instanceof MissingPlayersXxzTrfLine)
                .flatMap(it -> ((MissingPlayersXxzTrfLine) it).getPlayerIds().stream()).collect(Collectors.toSet());
        List<Integer> playerIds = TrfUtil.players(trf).map(Player001TrfLine::getStartingRank)
                .filter(it -> !missingPLayerIds.contains(it))
                .sorted().toList();

        List<Entry<Integer, Integer>> pairings = null;
        int skipRounds = 0;
        // assuming that if pairing with soft constraints is not found, we can drop first rounds to take fewer things into account.
        // assuming that a tournament with NO rounds played is always pairable.
        while (pairings == null && skipRounds < 1000) {
            pairings = tryPair(trf, playerIds, new HashSet<>(playerIds), skipRounds);
            skipRounds ++;
        }
        if (pairings == null) {
            throw new PairingFailedException("No pairings found");
        }
        Map<Integer, Integer> map = new LinkedHashMap<>();
        for (Entry<Integer, Integer> pairing : pairings) {
            if (map.put(pairing.getKey(), pairing.getValue()) != null) {
                throw new IllegalStateException("Duplicate key");
            }
        }
        return map;
    }

    @Nullable
    private static List<Entry<Integer, Integer>> tryPair(
            List<TrfLine> trf,
            List<Integer> playerIds,
            Set<Integer> unpairedPlayers,
            int skipRounds
    ) {
        if (unpairedPlayers.isEmpty()) {
            return Collections.emptyList();
        }
        for (int playerId1 : playerIds) {
            if (!unpairedPlayers.contains(playerId1)) {
                continue;
            }
            Player001TrfLine player1 = TrfUtil.player(trf, playerId1);
            for (int playerId2 : playerIds) {
                if (playerId1 == playerId2) {
                    continue;
                }
                if (!unpairedPlayers.contains(playerId2)) {
                    continue;
                }

                if (player1.getMatches().stream().skip(skipRounds).anyMatch(it -> it.getOpponentPlayerId() == playerId2)) {
                    continue;
                }

                if (TrfUtil.player(trf, playerId2).getMatches().stream().skip(skipRounds).anyMatch(it -> it.getOpponentPlayerId() == playerId1)) {
                    continue;
                }

                HashSet<Integer> unpairedPlayers2 = new HashSet<>(unpairedPlayers);
                unpairedPlayers2.remove(playerId1);
                unpairedPlayers2.remove(playerId2);

                Player001TrfLine player2 = TrfUtil.player(trf, playerId2);
                List<Entry<Integer, Integer>> entries = tryPair(trf, playerIds, unpairedPlayers2, skipRounds);
                if (entries != null) {
                    ArrayList<Entry<Integer, Integer>> entries1 = new ArrayList<>(entries);
                    var timesWhilePlayer1 = player1.getMatches().stream().skip(skipRounds).filter(it -> it.getColor() == 'w').count();
                    var timesWhilePlayer2 = player2.getMatches().stream().skip(skipRounds).filter(it -> it.getColor() == 'w').count();
                    if (timesWhilePlayer1 <= timesWhilePlayer2) {
                        entries1.add(0, new SimpleEntry<>(playerId1, playerId2));
                    } else {
                        entries1.add(0, new SimpleEntry<>(playerId2, playerId1));
                    }
                    return entries1;
                }
            }
            boolean neverHadPairingAllocatedBye = player1.getMatches().stream().skip(skipRounds).noneMatch(it -> it.getResult() == Player001TrfLine.TrfMatchResult.PAIRING_ALLOCATED_BYE.getCharCode());
            if (neverHadPairingAllocatedBye) {
                if (unpairedPlayers.size() == 1) {
                    return List.of(new SimpleEntry<>(unpairedPlayers.iterator().next(), 0));
                }
            }
        }
        return null;
    }
}
