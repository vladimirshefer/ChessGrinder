package com.chessgrinder.chessgrinder.trf.line;

import com.chessgrinder.chessgrinder.trf.dto.PlayerTrfLineDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTrfLineParserTest {
    @Test
    void testRead() {
        PlayerTrfLineParser parser = new PlayerTrfLineParser();
        String playerLine = "001    1      Test0001 Player0001               2573                             7.5    3";
        PlayerTrfLineDto player = parser.tryParse(playerLine);
        assertEquals(1, player.getStartingRank());
        assertEquals("Test0001 Player0001", player.getName());
        StringBuilder trf = new StringBuilder();
        parser.tryWrite(trf::append, player);
        assertEquals(playerLine, trf.toString());
    }

}
