package com.chessgrinder.chessgrinder.trf.line;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MissingPlayersTrfLineParserTest {

    @Test
    void testReadAndWrite() {
        var parser = new MissingPlayersTrfLineParser();
        String trfLine = "XXZ 101 14 90";
        var result = parser.tryParse(trfLine);
        assertEquals(Arrays.asList(101, 14, 90), result.getPlayerIds());
        StringBuilder trf = new StringBuilder();
        parser.tryWrite(trf::append, result);
        assertEquals(trfLine, trf.toString());
    }

}
