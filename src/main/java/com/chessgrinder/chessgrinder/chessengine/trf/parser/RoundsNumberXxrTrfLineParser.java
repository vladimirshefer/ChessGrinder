package com.chessgrinder.chessgrinder.chessengine.trf.parser;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.MissingPlayersXxzTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.RoundsNumberXxrTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;

import java.util.function.Consumer;

public class RoundsNumberXxrTrfLineParser implements TrfLineParser<RoundsNumberXxrTrfLine> {

    @Override
    public boolean canWrite(TrfLine lineDto) {
        return lineDto instanceof MissingPlayersXxzTrfLine;
    }

    @Override
    public RoundsNumberXxrTrfLine tryParse(String line) {
        if (!line.startsWith("XXR ")) {
            return null;
        }
        String roundsNumberString = line.substring(3).trim();
        int roundsNumber = Integer.parseInt(roundsNumberString);
        return RoundsNumberXxrTrfLine.of(roundsNumber);
    }

    @Override
    public void tryWrite(Consumer<String> trfConsumer, TrfLine line) {
        if (line instanceof RoundsNumberXxrTrfLine roundsLine) {
            trfConsumer.accept(roundsLine.toString());
        }
    }
}
