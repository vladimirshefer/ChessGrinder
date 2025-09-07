package com.chessgrinder.chessgrinder.chessengine.trf.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode
public class UnknownTrfLine implements WriteableTrfLine {
    private final String line;

    @Override
    public String toString() {
        return line;
    }

}
