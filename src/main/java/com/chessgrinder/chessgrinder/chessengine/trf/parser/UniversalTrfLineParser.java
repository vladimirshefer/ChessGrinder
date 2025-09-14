package com.chessgrinder.chessgrinder.chessengine.trf.parser;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The main implementation that combines all other implementations.
 */
public class UniversalTrfLineParser implements TrfLineParser<TrfLine> {

    private static final List<TrfLineParser<? extends TrfLine>> TRF_LINE_PARSERS = List.of(
            new MissingPlayersTrfLineParser(),
            new PlayerTrfLineParser(),
            new RoundsNumberXxrTrfLineParser(),
            new PointsForResultXxsTrfLineParser(),
            new WriteableTrfLineParser()
    );

    private final List<TrfLineParser<? extends TrfLine>> delegates;

    public UniversalTrfLineParser() {
        delegates = TRF_LINE_PARSERS;
    }

    public UniversalTrfLineParser(List<TrfLineParser<? extends TrfLine>> delegates) {
        this.delegates = delegates;
    }

    @Override
    public boolean canWrite(TrfLine lineDto) {
        return delegates.stream().anyMatch(delegate -> delegate.canWrite(lineDto));
    }

    @Override
    public TrfLine tryParse(String line) {
        return delegates.stream()
                .map(trfLineParser -> {
                    try {
                        return (TrfLine) trfLineParser.tryParse(line);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow();
    }

    @Override
    public void tryWrite(Consumer<String> trfConsumer, TrfLine line) {
        for (TrfLineParser<? extends TrfLine> trfLineParser : delegates) {
            if (trfLineParser.canWrite(line)) {
                trfLineParser.tryWrite(trfConsumer, line);
                return;
            }
        }
    }
}
