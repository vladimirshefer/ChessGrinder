package com.chessgrinder.chessgrinder.chessengine.trf.parser;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.PointsForResultXxsTrfLine;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PointsForResultXxsTrfLineParserTest {

    private final PointsForResultXxsTrfLineParser parser = new PointsForResultXxsTrfLineParser();

    @Test
    void test() {
        var parsed = parser.tryParse("XXS WW=1.0 BW=0.0 WD=0.6 BD=0.4 WL=0.2 BL=.3 ZPB=0.1 HPB=0.7 FPB=.8 PAB=0.9 FW=1.0 FL=0.0  \t W=1.0 D=0.5 L=0.0");
        Map<PointsForResultXxsTrfLine.Results, Double> expectedMap = new HashMap<>();
        expectedMap.put(PointsForResultXxsTrfLine.Results.WW, 1.0);
        expectedMap.put(PointsForResultXxsTrfLine.Results.BW, 0.0);
        expectedMap.put(PointsForResultXxsTrfLine.Results.WD, 0.6);
        expectedMap.put(PointsForResultXxsTrfLine.Results.BD, 0.4);
        expectedMap.put(PointsForResultXxsTrfLine.Results.WL, 0.2);
        expectedMap.put(PointsForResultXxsTrfLine.Results.BL, 0.3);
        expectedMap.put(PointsForResultXxsTrfLine.Results.ZPB, 0.1);
        expectedMap.put(PointsForResultXxsTrfLine.Results.HPB, 0.7);
        expectedMap.put(PointsForResultXxsTrfLine.Results.FPB, 0.8);
        expectedMap.put(PointsForResultXxsTrfLine.Results.PAB, 0.9);
        expectedMap.put(PointsForResultXxsTrfLine.Results.FW, 1.0);
        expectedMap.put(PointsForResultXxsTrfLine.Results.FL, 0.0);
        expectedMap.put(PointsForResultXxsTrfLine.Results.W, 1.0);
        expectedMap.put(PointsForResultXxsTrfLine.Results.D, 0.5);
        expectedMap.put(PointsForResultXxsTrfLine.Results.L, 0.0);
        assertEquals(parsed.getPointsForResult(), expectedMap);

        String toString = parser.writeAll(Collections.singletonList(parsed));
        assertEquals("XXS BD=0.4 BL=0.3 BW=0.0 D=0.5 FL=0.0 FPB=0.8 FW=1.0 HPB=0.7 L=0.0 PAB=0.9 W=1.0 WD=0.6 WL=0.2 WW=1.0 ZPB=0.1\n", toString);
    }

    @Test
    void testFailsIfDuplicate() {
        assertThrows(RuntimeException.class, () -> parser.tryParse("XXS WW=1.0 WW=2.0"));
    }

    @Test
    void testBigNumbers() {
        var parsed = parser.tryParse("XXS PAB=1000000 W=1234.56");
        Map<PointsForResultXxsTrfLine.Results, Double> expectedMap = new HashMap<>();
        expectedMap.put(PointsForResultXxsTrfLine.Results.PAB, 1000000.0);
        expectedMap.put(PointsForResultXxsTrfLine.Results.W, 1234.56);
        assertEquals(parsed.getPointsForResult(), expectedMap);
    }
}
