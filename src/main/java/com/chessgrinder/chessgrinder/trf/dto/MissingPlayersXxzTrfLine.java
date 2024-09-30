package com.chessgrinder.chessgrinder.trf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 * A way to register the players that are not going to be paired in
 * a round is to use the extension code XXZ, the format of which is:
 * XXZ  list-of-pairing-id(s)
 * </pre>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissingPlayersXxzTrfLine implements TrfLine {
    List<Integer> playerIds;
}
