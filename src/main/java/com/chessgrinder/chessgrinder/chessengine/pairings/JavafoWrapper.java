package com.chessgrinder.chessgrinder.chessengine.pairings;

import javafo.api.JaVaFoApi;

import java.io.ByteArrayInputStream;

public class JavafoWrapper {
    /**
     * JaVaFo is not thread-safe library, therefore to avoid concurrency problems,
     * the requests to JaVaFo are synchronized on this monitor.
     */
    private static final Object JAVAFO_MONITOR = new Object();

    public static String exec(ExecutionCodes code, String trf) {
        synchronized (JAVAFO_MONITOR) {
            return JaVaFoApi.exec(code.getCode(), new ByteArrayInputStream(trf.getBytes()));
        }
    }

    public enum ExecutionCodes {
        /** Standard pairing */
        PAIRING(1000),
        /** Standard pairing, using, if applicable, the Baku Acceleration Method */
        PAIRING_WITH_BAKU(1001),
        /** Check-list before doing the pairing Note: this operation is undocumented using JaVaFo as a stand-alone program */
        PRE_PAIRING_CHECKLIST(1100),
        /** Check-list after the pairing has been done */
        POST_PAIRING_CHECKLIST(1110),
        /** Check-list after the pairing has been done using, if applicable, the BakuAcceleration Method */
        POST_PAIRING_WITH_BAKU_CHECKLIST(1111),
        /** Check the correctness of a tournament */
        CHECK_TOURNAMENT(1200),
        /** Check the correctness of a single round of a tournament Note: this operation is not possible using JaVaFo as a stand-alone program */
        CHECK_ONE_ROUND(1210),
        /** Generate a random tournament */
        RANDOM_GENERATOR(1300),
        /** Generate a random tournament using, if applicable, the Baku Acceleration Method */
        RANDOM_GENERATOR_WITH_BAKU(1301);

        private final int code;

        ExecutionCodes(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public static ExecutionCodes of(int code) {
            for (ExecutionCodes e : values()) {
                if (e.code == code) {
                    return e;
                }
            }
            throw new IllegalArgumentException("Unknown code " + code);
        }
    }

}
