package com.chessgrinder.chessgrinder.trf.line;

import com.chessgrinder.chessgrinder.trf.dto.PlayerTrfLineDto;

import java.util.function.Consumer;

public class PlayerTrfLineParser implements TrfLineParser<PlayerTrfLineDto> {

    @Override
    public PlayerTrfLineDto tryParse(String line) {
        if (!line.startsWith("001 ")) {
            return null;
        }
        String pairingIdPart = line.substring(4, 8);
        String namePart = line.substring(14, 47);
        String ratingPart = line.substring(48, 52);
        String pointsPart = line.substring(80, 84);
        String rankPart = line.substring(85, 89);

        PlayerTrfLineDto result = PlayerTrfLineDto.builder()
                .startingRank(Integer.parseInt(pairingIdPart.trim()))
                .name(namePart.trim())
                .rating(parseInteger(ratingPart))
                .points(parseFloat(pointsPart))
                .rank(parseInteger(rankPart))
                .build();
        return result;
    }

    @Override
    public void tryWrite(Consumer<String> trfConsumer, PlayerTrfLineDto line) {
        trfConsumer.accept("001 ");
        trfConsumer.accept(toFixedLengthRight(line.getStartingRank(), 4, ' '));
        trfConsumer.accept(" ");
        trfConsumer.accept(toFixedLengthLeft(toStringOrEmpty(line.getSex()), 1, ' '));
        trfConsumer.accept(toFixedLengthLeft(toStringOrEmpty(line.getTitle()), 3, ' '));
        trfConsumer.accept(" ");
        String name = line.getName() != null ? line.getName() : line.getStartingRank() + "";
        trfConsumer.accept(toFixedLengthLeft(name, 34, ' '));
        trfConsumer.accept(toFixedLengthRight(toStringOrEmpty(line.getRating()), 4, ' '));
        trfConsumer.accept(toFixedLengthLeft("", 29, ' ')); // TODO insert federation, FIDE number, birthday
        trfConsumer.accept(toFixedLengthLeft(toStringOrEmpty(line.getPoints()), 4, ' '));
        trfConsumer.accept(toFixedLengthRight(toStringOrEmpty(line.getRank()), 4, ' '));
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
        sb.append(original);
        while (sb.length() < targetLength) {
            sb.append(gapChar);
        }
        return sb.toString();
    }

    public static Integer parseInteger(String str) {
        String trim = str.trim();
        if (trim.isEmpty()) {
            return null;
        }
        return Integer.parseInt(trim);
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

}
