package com.chessgrinder.chessgrinder.chessengine.trf.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoundsNumberXxrTrfLine implements WriteableTrfLine, CodedTrfLine {
    public static final String XXR = "XXR";
    private int roundsNumber;

    @Override
    public String toString() {
        return XXR + " " + roundsNumber;
    }

    @Override
    public String getCode() {
        return XXR;
    }

    public static RoundsNumberXxrTrfLine of(int roundsNumber) {
        return RoundsNumberXxrTrfLine.builder().roundsNumber(roundsNumber).build();
    }
}
