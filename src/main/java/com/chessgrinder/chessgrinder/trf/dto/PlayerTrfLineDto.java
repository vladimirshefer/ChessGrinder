package com.chessgrinder.chessgrinder.trf.dto;

import jakarta.annotation.Nullable;
import lombok.*;

import java.util.List;

/**
 * <pre>
 * Remark 1 Each line shall have a "CR" (carriage return) as last character.
 * Remark 2 The columns R and P in all the following tables tell the
 * importance of the field for Rating and Pairing respectively
 * ■ - Mandatory
 * □ - Warning if wrong
 * _ - Not taken into account
 *
 * Position  Description                  Contents              R P
 * 1 - 3     Data IdentificationNumber    001 (for player-data) ■ ■
 * 5 - 8     Startingrank                 Number from 1 to 9999 ■ ■
 * 10        Sex                          m/w                   □
 * 11 - 13   Title                        GM, IM, WGM,          □
 *                                        FM, WIM, CM, WFM, WCM
 * 15 - 47   Name                         Lastname, Firstname   □
 * 49 - 52   FIDE Rating                                        □
 * 54 - 56   FIDE Federation                                    □
 * 58 - 68   FIDE Number (including 3 digits reserve)           ■
 * 70 - 79   Birth Date Format: YYYY/MM/DD                      □
 * 81 - 84   Points Points (in the format 11.5)                 ■
 *           This is the number of points in the tournament
 *           standings, which depends on the scoring
 *           points system used and on the value of the
 *           pairing-allocated bye (usually the same as a
 *           win). If, for instance, the 3/1/0 scoring
 *           point system is applied
 *           in a tournament and a
 *           player scored 5 wins, 2 draws and 2 losses,
 *           this field should contain "17.0"
 * 86 - 89   Rank Exact definition, especially for Team         ■
 * </pre>
 *
 * @see com.chessgrinder.chessgrinder.trf.line.PlayerTrfLineParser
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerTrfLineDto {

    private int startingRank;

    @Nullable
    private Integer rating;

    /**
     * "w"/"m"/null
     */
    @Nullable
    private String sex;

    @Nullable
    private String title;

    private String name;

    private Float points;

    private Integer rank;

    private List<Match> matches;

    /**
     * <pre>
     *
     *For each round:
     *
     * Position   Description Contents                                         R P
     * 92 - 95    Player or forfeit id                                         ■ ■
     *            in round 1
     *            "____"  Startingrank-Number of the scheduled opponent
     *                    (up to 4 digits)
     *            "0000"  If the player had a bye (either half-point bye,
     *                    full-point bye or odd-number bye)
     *                    or was not paired (absent, retired, not
     *                    nominated by team)
     *            "    "  (four blanks) equivalent to 0000
     *
     * 97         Scheduled color or forfeit in round                          ■ ■
     *            "w"/"b" Scheduled color against the scheduled opponent
     *            "-"     (minus) If the player had a bye or was not paired
     *            " "     (blank) equivalent to -
     *
     * 99         Result of round 1                                            ■ ■
     *            The scheduled game was not played
     *            "-" Forfeit loss
     *            "+" Forfeit win
     *            The scheduled game lasted less than one move
     *            "W" Win Not rated
     *            "D" Draw Not rated
     *            "L" Loss Not rated
     *            Regular game
     *            "1" Win
     *            "=" Draw
     *            "0" Loss
     *            Bye
     *            "H" Half-point-bye Not rated
     *            "F" Full-point-bye Not rated
     *            "U" Pairing-allocated bye At most once for round - Not rated
     *                (U for player unpaired by the system)
     *            "Z" Zero-point-bye Known absence from round - Not rated
     *            " " (blank) equivalent to Z
     *            Note: Letter codes are case-insensitive
     *            (i.e. w,d,l,h,f,u,z can be used)
     *
     * Next rounds are the same:
     * Round 2 (analog to round 1)
     * 102 - 105  Id        ■ ■
     * 107        Color     ■ ■
     * 109        Result    ■ ■
     * Round 3 (analog to round 1)
     * 112 - 115  Id        ■ ■
     * 117        Color     ■ ■
     * 119        Result    ■ ■
     * And so on...
     * </pre>
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Match {
        private int opponentPlayerId;
        private char color;
        private char result;
    }

    public static enum TrfMatchResult {
        /**
         * The scheduled game was not played
         */
        FORFEIT_LOSS("-"),
        /**
         * The scheduled game was not played
         */
        FORFEIT_WIN("+"),
        /**
         * The scheduled game lasted less than one move
         * Not rated
         */
        QUICK_WIN("W"),
        /**
         * The scheduled game lasted less than one move
         * Not rated
         */
        QUICK_DRAW("D"),
        /**
         * The scheduled game lasted less than one move
         * Not rated
         */
        QUICK_LOSS("L"),
        /**
         * Regular game
         */
        WIN("1"),
        /**
         * Regular game
         */
        DRAW("="),
        /**
         * Regular game
         */
        LOSS("0"),
        /**
         * Not rated
         */
        HALF_POINT_BYE("H"),
        /**
         * Not rated
         */
        FULL_POINT_BYE("F"),
        /**
         * At most once for round - Not rated
         */
        PAIRING_ALLOCATED_BYE("U"),
        /**
         * Known absence from round - Not rated
         */
        ZERO_POINT_BYE("Z"),
        ;

        TrfMatchResult(String code) {
            this.code = code;
        }

        @Getter
        private String code;

        public char getCharCode(){
            return code.charAt(0);
        };

    }
}
