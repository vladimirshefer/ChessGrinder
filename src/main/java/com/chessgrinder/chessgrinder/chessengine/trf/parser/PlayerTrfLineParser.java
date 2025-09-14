package com.chessgrinder.chessgrinder.chessengine.trf.parser;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.Player001TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlayerTrfLineParser implements TrfLineParser<Player001TrfLine> {

    @Override
    public boolean canWrite(TrfLine lineDto) {
        return lineDto instanceof Player001TrfLine;
    }

    @Override
    public Player001TrfLine tryParse(String line) {
        if (!line.startsWith("001 ")) {
            return null;
        }
        String pairingIdPart = line.substring(4, 8);
        String namePart = line.substring(14, 47);
        String ratingPart = line.substring(48, 52);
        /*
         DANGER: By TRF documentation the points are in position 81-84 (1-based).
         But JaVaFo random tournament generator actually generates the points on positions 81-85.
         Looks like either a bug in JaVaFo, or a mistake in TRF docs.
         Char 85 is empty anyway (space), so taking char at position 85 (1-based) looks safe.
        */
        String pointsPart = line.substring(80, 84 + 1);
        String rankPart = line.substring(85, 89);

        Player001TrfLine result = Player001TrfLine.builder()
                .startingRank(Integer.parseInt(pairingIdPart.trim()))
                .name(namePart.trim())
                .rating(parseInteger(ratingPart))
                .points(parseFloat(pointsPart))
                .rank(parseInteger(rankPart))
                .build();

        List<Player001TrfLine.Match> matches = new ArrayList<>();
        int nextRoundPosition = 91;
        while (line.trim().length() >= nextRoundPosition + 8) {
            String matchPart = line.substring(nextRoundPosition, nextRoundPosition + 8);
            //noinspection DataFlowIssue
            matches.add(
                    Player001TrfLine.Match.builder()
                            .opponentPlayerId(parseInteger(matchPart.substring(0, 4), 0))
                            .color(matchPart.substring(5, 6).charAt(0))
                            .result(matchPart.substring(7, 8).charAt(0))
                            .build()
            );
            nextRoundPosition += 10;
        }

        result.setMatches(matches);

        return result;
    }

    @Override
    public void tryWrite(Consumer<String> trfConsumer, TrfLine line) {
        if (!(line instanceof Player001TrfLine playerLine)) {
            return;
        }
        trfConsumer.accept("001 ");
        trfConsumer.accept(toFixedLengthRight(playerLine.getStartingRank(), 4, ' '));
        trfConsumer.accept(" ");
        trfConsumer.accept(toFixedLengthLeft(toStringOrEmpty(playerLine.getSex()), 1, ' '));
        trfConsumer.accept(toFixedLengthLeft(toStringOrEmpty(playerLine.getTitle()), 3, ' '));
        trfConsumer.accept(" ");
        String name = playerLine.getName() != null ? playerLine.getName() : playerLine.getStartingRank() + "";
        trfConsumer.accept(toFixedLengthLeft(name, 33, ' '));
        trfConsumer.accept(" ");
        trfConsumer.accept(toFixedLengthRight(toStringOrEmpty(playerLine.getRating()), 4, ' '));
        trfConsumer.accept(toFixedLengthLeft("", 29, ' ')); // TODO insert federation, FIDE number, birthday
        trfConsumer.accept(toFixedLengthLeft(toStringOrEmpty(playerLine.getPoints()), 4, ' '));
        trfConsumer.accept(" ");
        trfConsumer.accept(toFixedLengthRight(toStringOrEmpty(playerLine.getRank()), 3, ' '));
        trfConsumer.accept("  ");

        if (playerLine.getMatches() != null && !playerLine.getMatches().isEmpty()) {
            for (Player001TrfLine.Match match : playerLine.getMatches()) {
                trfConsumer.accept(toFixedLengthRight(match.getOpponentPlayerId() != 0 ? match.getOpponentPlayerId() : "0000", 4, ' '));
                trfConsumer.accept(" " + match.getColor() + " " + match.getResult() + "  ");
            }
        }
    }

    public static String toFixedLengthRight(Object original, int targetLength, char gapChar) {
        StringBuilder sb = new StringBuilder();
        String str = String.valueOf(original);
        while (sb.length() < targetLength - str.length()) {
            sb.append(gapChar);
        }
        sb.append(str);
        return sb.toString();
    }

    public static String toFixedLengthLeft(Object original, int targetLength, char gapChar) {
        StringBuilder sb = new StringBuilder();
        String originalString = String.valueOf(original);
        sb.append(originalString, 0, Math.min(originalString.length(), targetLength));
        while (sb.length() < targetLength) {
            sb.append(gapChar);
        }
        return sb.toString();
    }

    public static Integer parseInteger(String str) {
        return parseInteger(str, null);
    }

    @Nullable
    private static Integer parseInteger(String str, @Nullable Integer defaultValue) {
        String trim = str.trim();
        if (trim.isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(trim);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static Float parseFloat(String str) {
        String trim = str.trim();
        if (trim.isEmpty()) {
            return null;
        }
        try {
            return Float.parseFloat(trim);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String toStringOrEmpty(Object any) {
        if (any == null) {
            return "";
        }
        return String.valueOf(any);
    }

    public static String toStringOrEmpty(Float number) {
        if (number == null) {
            return "";
        }
        return String.format("%.1f", number);
    }

}
