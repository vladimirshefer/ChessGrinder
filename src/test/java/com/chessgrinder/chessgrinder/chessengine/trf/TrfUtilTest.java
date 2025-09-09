package com.chessgrinder.chessgrinder.chessengine.trf;

import com.chessgrinder.chessgrinder.chessengine.javafo.JaVaFoWrapper;
import com.chessgrinder.chessgrinder.chessengine.trf.util.TrfUtil;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

class TrfUtilTest {

    @Test
    void testClone() {
        String trf = JaVaFoWrapper.execGenerate(new Properties());
        List<TrfLine> trfLines = TrfUtil.readTrf(trf);
        Assertions.assertThatList(TrfUtil.readTrf(TrfUtil.writeTrfLines(trfLines))).isEqualTo(trfLines);
    }
}
