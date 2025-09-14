package com.chessgrinder.chessgrinder.chessengine.pairings;

import com.chessgrinder.chessgrinder.chessengine.PairingFailedException;
import com.chessgrinder.chessgrinder.chessengine.javafo.JaVaFoWrapper;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.util.TrfUtil;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class JaVaFoPairingStrategyImpl implements PairingStrategy {

    private static final String NEWLINE_REGEX = "\\r?\\n|\\r";

    @Override
    public Map<Integer, Integer> makePairings(List<TrfLine> trf) {
        String pairingsString;
        try {
            pairingsString = JaVaFoWrapper.exec(JaVaFoWrapper.ExecutionCodes.PAIRING, TrfUtil.writeTrfLines(trf));
        } catch (Exception e) {
            throw new PairingFailedException("Could not do pairing via javafo. \n" + TrfUtil.writeTrfLines(trf), e);
        }
        String[] pairings = pairingsString.split(NEWLINE_REGEX);
        Map<Integer, Integer> result = new LinkedHashMap<>();
        for (int i = 1; i < pairings.length; i++) {
            String[] pairing = pairings[i].split(" ");
            result.put(Integer.valueOf(pairing[0]), Integer.valueOf(pairing[1]));
        }
        return result;
    }

}
