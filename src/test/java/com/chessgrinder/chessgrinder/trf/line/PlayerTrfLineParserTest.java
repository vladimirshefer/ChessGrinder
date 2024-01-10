package com.chessgrinder.chessgrinder.trf.line;

import com.chessgrinder.chessgrinder.trf.dto.PlayerTrfLineDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTrfLineParserTest {
    @Test
    void testReadAndWrite() {
        PlayerTrfLineParser parser = new PlayerTrfLineParser();
        String playerLine = "001    1      Test0001 Player0001               2573                             7.5    3  0000 - H    73 b 1    55 w 1    37 w =    26 b =    13 w =    38 b 1    19 w 1    17 b =    11 w 1  ";
        PlayerTrfLineDto player = parser.tryParse(playerLine);
        assertEquals(1, player.getStartingRank());
        assertEquals("Test0001 Player0001", player.getName());
        assertEquals(2573, player.getRating());
        assertEquals(7.5, player.getPoints(), 0.00001);
        assertEquals(3, player.getRank());
        assertEquals(10, player.getMatches().size());
        assertEquals(0, player.getMatches().get(0).getOpponentPlayerId());
        assertEquals('-', player.getMatches().get(0).getColor());
        assertEquals('H', player.getMatches().get(0).getResult());
        StringBuilder trf = new StringBuilder();
        parser.tryWrite(trf::append, player);
        assertEquals(playerLine, trf.toString());
    }

}
