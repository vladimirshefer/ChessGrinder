package com.chessgrinder.chessgrinder.trf.line;

import com.chessgrinder.chessgrinder.trf.dto.MissingPlayersTrfLine;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MissingPlayersTrfLineParser implements TrfLineParser<MissingPlayersTrfLine> {
    @Override
    public MissingPlayersTrfLine tryParse(String line) {
        if (!line.startsWith("XXZ ")) {
            return null;
        }
        String playerIdsString = line.substring(3).trim();

        List<Integer> playerIds = Arrays.stream(playerIdsString.split("\\W"))
                .map(String::trim)
                .filter(it -> !it.isBlank())
                .map(Integer::valueOf)
                .toList();

        return MissingPlayersTrfLine.builder().playerIds(playerIds).build();
    }

    @Override
    public void tryWrite(Consumer<String> trfConsumer, MissingPlayersTrfLine line) {
        if (line == null || line.getPlayerIds() == null || line.getPlayerIds().isEmpty()) {
            return;
        }

        trfConsumer.accept("XXZ ");
        trfConsumer.accept(line.getPlayerIds().stream().map(Object::toString).collect(Collectors.joining(" ")));
    }
}
