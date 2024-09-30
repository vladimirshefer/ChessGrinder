package com.chessgrinder.chessgrinder.trf.line;

import com.chessgrinder.chessgrinder.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.trf.dto.WriteableTrfLine;

import java.util.function.Consumer;

public class WriteableTrfLineParser implements TrfLineParser<WriteableTrfLine> {

    @Override
    public boolean canWrite(TrfLine lineDto) {
        return lineDto instanceof WriteableTrfLine;
    }

    @Override
    public WriteableTrfLine tryParse(String line) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void tryWrite(Consumer<String> trfConsumer, TrfLine line) {
        trfConsumer.accept(line.toString());
    }
}
