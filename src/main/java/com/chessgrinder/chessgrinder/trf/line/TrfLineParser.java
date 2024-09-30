package com.chessgrinder.chessgrinder.trf.line;

import com.chessgrinder.chessgrinder.trf.dto.TrfLine;

import java.util.function.Consumer;

public interface TrfLineParser<T extends TrfLine> {
    boolean canWrite(TrfLine lineDto);

    T tryParse(String line);
    void tryWrite(Consumer<String> trfConsumer, TrfLine line);
}
