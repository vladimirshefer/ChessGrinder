package com.chessgrinder.chessgrinder.testutil.trf;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.util.TrfUtil;
import org.junit.jupiter.api.Assertions;

import java.util.List;
import java.util.stream.Collectors;

public class TrfTestUtil {
    public static void assertTrfEquals(String trf, List<TrfLine> trfLines) {
        String actualStr = TrfUtil.writeTrfLines(trfLines);
        Assertions.assertEquals(
                trf.lines().map(String::trim).sorted().collect(Collectors.joining("\n")),
                actualStr.lines().map(String::trim).sorted().collect(Collectors.joining("\n")),
                "\n" + actualStr
        );
    }
}
