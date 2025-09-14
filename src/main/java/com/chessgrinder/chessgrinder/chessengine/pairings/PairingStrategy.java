package com.chessgrinder.chessgrinder.chessengine.pairings;

import com.chessgrinder.chessgrinder.chessengine.PairingFailedException;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import io.opentelemetry.instrumentation.annotations.WithSpan;

import java.util.*;

public interface PairingStrategy {
    /**
     * Produces the pairings for the next round.
     * <p>
     * The key is the pairing id (1-based) of the player to play white.
     * The value is the pairing id (1-based) of the player to play black.
     * <p>
     * 0 value in key or value means Pairing Allocated Bye (PAB, U) for the player.
     * Usually pairings produce zero to one PAB per round, with possible exceptions depending on the implementation.
     * <p>
     * The resulting map is likely to be sorted by table number:
     * Stronger players usually play on the first board.
     *
     * @param trf the tournament in TRFx notation
     * @throws PairingFailedException if the pairing engine could not produce the pairings due to a malformed input or impossible solution.
     * @return Map {player_a: player_b, player_c: player_d,...}
     */
    @WithSpan
    Map<Integer, Integer> makePairings(List<TrfLine> trf) throws PairingFailedException;
}
