package com.chessgrinder.chessgrinder.chessengine.trf.parser;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.MissingPlayersXxzTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.PointsForResultXxsTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PointsForResultXxsTrfLineParser implements TrfLineParser<PointsForResultXxsTrfLine> {
    @Override
    public boolean canWrite(TrfLine lineDto) {
        return lineDto instanceof PointsForResultXxsTrfLine;
    }

    @Override
    public PointsForResultXxsTrfLine tryParse(String line) {
        if (!line.startsWith("XXS ")) {
            return null;
        }
        String playerIdsString = line.substring(3).trim();

        Map<PointsForResultXxsTrfLine.Results, Double> map = Arrays.stream(playerIdsString.split("\\s"))
                .map(String::trim)
                .filter(it -> !it.isBlank())
                .map(it -> it.split("="))
                .collect(Collectors.toMap(
                        it -> PointsForResultXxsTrfLine.Results.valueOf(it[0]),
                        it -> it[1].startsWith(".") ? Double.valueOf("0" + it[1]) : Double.valueOf(it[1])
                ));

        return new PointsForResultXxsTrfLine(map);
    }

    @Override
    public void tryWrite(Consumer<String> trfConsumer, TrfLine line) {
        if (!(line instanceof PointsForResultXxsTrfLine trfLine)) {
            return;
        }
        trfConsumer.accept("XXS ");

        if (trfLine.getPointsForResult() == null || trfLine.getPointsForResult().isEmpty()) {
            return;
        }

        trfConsumer.accept(trfLine.getPointsForResult().entrySet().stream().map(it -> it.getKey().name() + "=" + String.format("%.1f", it.getValue())).sorted().collect(Collectors.joining(" ")));
    }
}
