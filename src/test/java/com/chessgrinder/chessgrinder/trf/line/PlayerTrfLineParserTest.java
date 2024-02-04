package com.chessgrinder.chessgrinder.trf.line;

import com.chessgrinder.chessgrinder.trf.dto.PlayerTrfLineDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTrfLineParserTest {
    @Test
    void testReadAndWrite() {
        var parser = new PlayerTrfLineParser();
        String trfLine = "001    1      Test0001 Player0001               2573                             7.5    3  0000 - H    73 b 1    55 w 1    37 w =    26 b =    13 w =    38 b 1    19 w 1    17 b =    11 w 1  ";
        var result = parser.tryParse(trfLine);
        assertEquals(1, result.getStartingRank());
        assertEquals("Test0001 Player0001", result.getName());
        assertEquals(2573, result.getRating());
        assertEquals(7.5, result.getPoints(), 0.00001);
        assertEquals(3, result.getRank());
        assertEquals(10, result.getMatches().size());
        assertEquals(0, result.getMatches().get(0).getOpponentPlayerId());
        assertEquals('-', result.getMatches().get(0).getColor());
        assertEquals('H', result.getMatches().get(0).getResult());
        StringBuilder trf = new StringBuilder();
        parser.tryWrite(trf::append, result);
        assertEquals(trfLine, trf.toString());
    }

}
