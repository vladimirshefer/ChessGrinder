package com.chessgrinder.chessgrinder.trf;

import com.chessgrinder.chessgrinder.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.trf.line.MissingPlayersTrfLineParser;
import com.chessgrinder.chessgrinder.trf.line.PlayerTrfLineParser;
import com.chessgrinder.chessgrinder.trf.line.TrfLineParser;
import com.chessgrinder.chessgrinder.trf.line.WriteableTrfLineParser;

import java.util.List;

public class TrfUtil {
    public static String writeTrfLines(List<TrfLine> trfLines) {
        List<TrfLineParser<? extends TrfLine>> trfLineParsers = List.of(
                new MissingPlayersTrfLineParser(),
                new PlayerTrfLineParser(),
                new WriteableTrfLineParser()
        );

        StringBuilder stringBuilder = new StringBuilder();

        for (TrfLine trfLine : trfLines) {
            for (TrfLineParser<? extends TrfLine> trfLineParser : trfLineParsers) {
                if (trfLineParser.canWrite(trfLine)) {
                    trfLineParser.tryWrite(stringBuilder::append, trfLine);
                    stringBuilder.append("\n");
                }
            }
        }

        return stringBuilder.toString();
    }
}
