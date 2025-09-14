package com.chessgrinder.chessgrinder.chessengine.trf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

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
public class MissingPlayersXxzTrfLine implements CodedTrfLine {
    List<Integer> playerIds;

    @Override
    public String getCode() {
        return "XXZ";
    }

    @Override
    public String toString() {
        return getCode() + " " + playerIds.stream().map(String::valueOf).collect(Collectors.joining(" "));
    }

    public static MissingPlayersXxzTrfLine of(List<Integer> playerIds) {
        return MissingPlayersXxzTrfLine.builder().playerIds(playerIds).build();
    }
}
