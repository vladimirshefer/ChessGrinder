package com.chessgrinder.chessgrinder.chessengine.pairings;

import com.chessgrinder.chessgrinder.chessengine.PairingFailedException;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.RoundsNumberXxrTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.util.TrfUtil;
import com.chessgrinder.chessgrinder.exceptions.PairingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultPairingStrategyImpl implements PairingStrategy {

    private final JaVaFoPairingStrategyImpl javaFoPairingStrategy;
    private final SimplePairingStrategyImpl simplePairingStrategy;

    @Override
    public Map<Integer, Integer> makePairings(List<TrfLine> trf) throws PairingFailedException {
        Map<Integer, Integer> result = null;

        for (int i : List.of(0, 1, 2, 10, 50)) {
            try {
                List<TrfLine> clone = new ArrayList<>(TrfUtil.clone(trf));
                List<RoundsNumberXxrTrfLine> xxr = TrfUtil.filterByType(clone, RoundsNumberXxrTrfLine.class).toList();
                var roundsNumber = 99;
                if (!xxr.isEmpty()) {
                    roundsNumber = xxr.get(0).getRoundsNumber();
                }
                clone.removeAll(xxr);
                clone.add(RoundsNumberXxrTrfLine.of(roundsNumber + i));
                result = javaFoPairingStrategy.makePairings(trf);
            } catch (PairingException pairingException) {
                log.error("Could not do pairing via javafo. Trying other pairing instead. {}", pairingException.getMessage(), pairingException);
            }
        }

        if (result != null && !result.isEmpty()) {
            return result;
        }

        try {
            return simplePairingStrategy.makePairings(trf);
        } catch (PairingException pairingException) {
            log.error("Could not do pairing via simple. {}", pairingException.getMessage(), pairingException);
        }

        if (result != null && !result.isEmpty()) {
            return result;
        }

        throw new PairingFailedException("Could not do pairing. No pairing engine could produce the pairings.");
    }

}
