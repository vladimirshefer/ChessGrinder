package com.chessgrinder.chessgrinder.chessengine.pairings;

import com.chessgrinder.chessgrinder.chessengine.trf.dto.RoundsNumberXxrTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.exceptions.PairingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DefaultPairingStrategyImplTest {

    private JaVaFoPairingStrategyImpl javaFoPairingStrategy;
    private SimplePairingStrategyImpl simplePairingStrategy;
    private DefaultPairingStrategyImpl defaultPairingStrategy;

    @BeforeEach
    void setUp() {
        javaFoPairingStrategy = mock(JaVaFoPairingStrategyImpl.class);
        simplePairingStrategy = mock(SimplePairingStrategyImpl.class);
        defaultPairingStrategy = new DefaultPairingStrategyImpl(javaFoPairingStrategy, simplePairingStrategy);
    }

    @Test
    void testJavafoSucceedsImmediately() throws Exception {
        List<TrfLine> trf = List.of(RoundsNumberXxrTrfLine.of(3));
        Map<Integer, Integer> expectedPairings = Map.of(1, 2);

        when(javaFoPairingStrategy.makePairings(any())).thenReturn(expectedPairings);

        Map<Integer, Integer> result = defaultPairingStrategy.makePairings(trf);

        assertEquals(expectedPairings, result);
        verify(javaFoPairingStrategy, times(1)).makePairings(any());
        verifyNoInteractions(simplePairingStrategy);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testJavafoFailsFirstThenSucceedsWithRetry() throws Exception {
        List<TrfLine> trf = List.of(RoundsNumberXxrTrfLine.of(3));
        Map<Integer, Integer> expectedPairings = Map.of(1, 2);

        // Fail for i=0 (roundsNumber = 3) and i=1 (roundsNumber = 4), but succeed for i=2 (roundsNumber = 5)
        when(javaFoPairingStrategy.makePairings(any()))
                .thenAnswer(invocation -> {
                    List<TrfLine> passedTrf = invocation.getArgument(0);
                    RoundsNumberXxrTrfLine xxr = (RoundsNumberXxrTrfLine) passedTrf.stream()
                            .filter(line -> line instanceof RoundsNumberXxrTrfLine)
                            .findFirst()
                            .orElseThrow();
                    if (xxr.getRoundsNumber() < 5) {
                        throw new PairingException("Simulated javafo failure");
                    }
                    return expectedPairings;
                });

        Map<Integer, Integer> result = defaultPairingStrategy.makePairings(trf);

        assertEquals(expectedPairings, result);

        // Verify javafo was called three times with the correct incremented rounds (3, 4, then 5)
        ArgumentCaptor<List<TrfLine>> captor = ArgumentCaptor.forClass(List.class);
        verify(javaFoPairingStrategy, times(3)).makePairings(captor.capture());

        List<List<TrfLine>> allValues = captor.getAllValues();
        assertEquals(3, getRoundsNumber(allValues.get(0)));
        assertEquals(4, getRoundsNumber(allValues.get(1)));
        assertEquals(5, getRoundsNumber(allValues.get(2)));

        verifyNoInteractions(simplePairingStrategy);
    }

    @Test
    void testJavafoFailsCompletelyAndFallsBackToSimple() throws Exception {
        List<TrfLine> trf = List.of(RoundsNumberXxrTrfLine.of(3));
        Map<Integer, Integer> expectedPairings = Map.of(3, 4);

        when(javaFoPairingStrategy.makePairings(any())).thenThrow(new PairingException("Simulated javafo failure"));
        when(simplePairingStrategy.makePairings(any())).thenReturn(expectedPairings);

        Map<Integer, Integer> result = defaultPairingStrategy.makePairings(trf);

        assertEquals(expectedPairings, result);
        verify(javaFoPairingStrategy, times(5)).makePairings(any()); // 0, 1, 2, 10, 50 retries
        verify(simplePairingStrategy, times(1)).makePairings(trf);
    }

    private int getRoundsNumber(List<TrfLine> trfLines) {
        return trfLines.stream()
                .filter(line -> line instanceof RoundsNumberXxrTrfLine)
                .map(line -> ((RoundsNumberXxrTrfLine) line).getRoundsNumber())
                .findFirst()
                .orElse(-1);
    }
}
