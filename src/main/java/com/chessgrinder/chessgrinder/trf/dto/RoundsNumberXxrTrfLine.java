package com.chessgrinder.chessgrinder.trf.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoundsNumberXxrTrfLine implements WriteableTrfLine {
    private int roundsNumber;

    @Override
    public String toString() {
        return "XXR " + roundsNumber;
    }
}
