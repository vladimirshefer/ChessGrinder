package com.chessgrinder.chessgrinder.chessengine.trf.parser;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.MissingPlayersXxzTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

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

        List<Integer> playerIds = parseIntegers(playerIdsString);

        return MissingPlayersXxzTrfLine.of(playerIds);
    }

    /**
     * @param string example: "    1 \n \t 678 039  9129  912  "
     * @return example: [1, 678, 39, 9129, 912]
     */
    private static List<Integer> parseIntegers(String string) {
        return Arrays.stream(string.split("\\W"))
                .map(String::trim)
                .filter(it -> !it.isBlank())
                .map(Integer::valueOf)
                .toList();
    }

    @Override
    public void tryWrite(Consumer<String> trfConsumer, TrfLine line) {
        trfConsumer.accept(line.toString());
    }
}
