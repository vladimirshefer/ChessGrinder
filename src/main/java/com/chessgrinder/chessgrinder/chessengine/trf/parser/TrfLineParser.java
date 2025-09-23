package com.chessgrinder.chessgrinder.chessengine.trf.parser;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public interface TrfLineParser<T extends TrfLine> {

    boolean canWrite(TrfLine lineDto);

    T tryParse(String line);
    void tryWrite(Consumer<String> trfConsumer, TrfLine line);
    default List<T> parseAll(String trf) {
        return trf.lines().map(this::tryParse).filter(Objects::nonNull).toList();
    }

    default String writeAll(List<? extends T> trf) {
        StringBuilder sb = new StringBuilder();
        for (TrfLine trfLine : trf) {
            if (canWrite(trfLine)) {
                tryWrite(sb::append, trfLine);
                sb.append("\n");
            }
        }
        return sb.toString();
    }

}
