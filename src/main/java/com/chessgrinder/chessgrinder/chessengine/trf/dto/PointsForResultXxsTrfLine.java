package com.chessgrinder.chessgrinder.chessengine.trf.dto;

import lombok.Data;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * XXS  CODE=VALUE CODE=VALUE CODE=VALUE
 */
@Data
public class PointsForResultXxsTrfLine implements CodedTrfLine {
    private Map<Results, Double> pointsForResult;

    public PointsForResultXxsTrfLine(Map<Results, Double> pointsForResult) {
        this.pointsForResult = pointsForResult;
    }

    public Map<Results, Double> getPointsForResult() {
        return Collections.unmodifiableMap(pointsForResult);
    }

    @Override
    public String getCode() {
        return "XXS";
    }

    public enum Results {
        /**  Points for win with White */
        WW (1.0),
        /**  Points for win with Black */
        BW (1.0),
        /**  Points for draw with White */
        WD (0.5),
        /**  Points for draw with Black */
        BD (0.5),
        /**  Points for loss with White */
        WL (0.0),
        /**  Points for loss with Black */
        BL (0.0),
        /**  Points for zero-point-bye */
        ZPB (0.0),
        /**  Points for half-point-bye */
        HPB (0.5),
        /**  Points for full-point-bye */
        FPB (1.0),
        /**  Points for pairing-allocated-bye */
        PAB (1.0),
        /**  Points for forfeit win */
        FW (1.0),
        /**  Points for forfeit loss */
        FL (0.0),
        /**  Encompasses all the codes WW, BW, FW, FPB */
        W (1.0),
        /**  Encompasses all the codes WD, BD, HPB */
        D (0.5),
        /**  Encompasses all the codes WL, BL
         * In real JaVaFo documentation is commented. Use with caution!*/
        @Deprecated
        L (0.0),
        ;

        public static Map<Results, Double> getDefaultPointsForResult() {
            return new HashMap<>() {{
                Arrays.stream(Results.values()).forEach(it ->
                        put(it, it.getDefaultValue())
                );
            }};
        }

        private final double defaultValue;

        Results(double defaultValue) {
            this.defaultValue = defaultValue;
        }

        public double getDefaultValue() {
            return defaultValue;
        }
    }
}
