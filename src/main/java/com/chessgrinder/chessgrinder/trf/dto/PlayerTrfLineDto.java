package com.chessgrinder.chessgrinder.trf.dto;

import jakarta.annotation.Nullable;
import lombok.*;

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
@EqualsAndHashCode
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

}
