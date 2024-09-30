package com.chessgrinder.chessgrinder.trf.line;

import com.chessgrinder.chessgrinder.trf.dto.MissingPlayersXxzTrfLine;
import com.chessgrinder.chessgrinder.trf.dto.TrfLine;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MissingPlayersTrfLineParser implements TrfLineParser<MissingPlayersXxzTrfLine> {

    @Override
    public boolean canWrite(TrfLine lineDto) {
        return lineDto instanceof MissingPlayersXxzTrfLine;
    }

    @Override
    public MissingPlayersXxzTrfLine tryParse(String line) {
        if (!line.startsWith("XXZ ")) {
            return null;
        }
        String playerIdsString = line.substring(3).trim();

        List<Integer> playerIds = Arrays.stream(playerIdsString.split("\\W"))
                .map(String::trim)
                .filter(it -> !it.isBlank())
                .map(Integer::valueOf)
                .toList();

        return MissingPlayersXxzTrfLine.builder().playerIds(playerIds).build();
    }

    @Override
    public void tryWrite(Consumer<String> trfConsumer, TrfLine line) {
        if (!(line instanceof MissingPlayersXxzTrfLine trfLine)) {
            return;
        }

        if (trfLine.getPlayerIds() == null || trfLine.getPlayerIds().isEmpty()) {
            return;
        }

        trfConsumer.accept("XXZ ");
        trfConsumer.accept(trfLine.getPlayerIds().stream().map(Object::toString).collect(Collectors.joining(" ")));
    }
}
