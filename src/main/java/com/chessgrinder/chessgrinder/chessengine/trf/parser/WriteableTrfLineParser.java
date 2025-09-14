package com.chessgrinder.chessgrinder.chessengine.trf.parser;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.UnknownTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.WriteableTrfLine;

import java.util.function.Consumer;

public class WriteableTrfLineParser implements TrfLineParser<WriteableTrfLine> {

    @Override
    public boolean canWrite(TrfLine lineDto) {
        return lineDto instanceof WriteableTrfLine;
    }

    @Override
    public WriteableTrfLine tryParse(String line) {
        return new UnknownTrfLine(line);
    }

    @Override
    public void tryWrite(Consumer<String> trfConsumer, TrfLine line) {
        trfConsumer.accept(line.toString());
    }

}
