package com.chessgrinder.chessgrinder.comparator;

import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MatchEntityComparators {

    /**
     * First are meaningful results (WIN, LOSS, DRAW, UNKNOWN), then BYE, then MISS
     */
    public static final Comparator<MatchEntity> COMPARE_MATCH_ENTITY_BY_RESULT_MEANINGFUL_FIRST = Comparator.comparing(
            it -> Optional.ofNullable(it)
                    .map(MatchEntity::getResult)
                    .map(result -> switch (result) {
                        case MISS -> BigDecimal.valueOf(3); // MISS is in the end
                        case BUY -> BigDecimal.valueOf(2); // BYE is after all meaningful results
                        default -> BigDecimal.valueOf(0); // all other results (e.g. WHITE_WIN, DRAW, unknown, etc.)
                    })
                    .orElse(BigDecimal.valueOf(0)),
            nullsLast(naturalOrder())
    );

    public static final Comparator<MatchEntity> COMPARE_MATCH_ENTITY_BY_NICKNAME1 = Comparator.comparing(
            it -> Optional.ofNullable(it)
                    .map(MatchEntity::getParticipant1)
                    .map(ParticipantEntity::getNickname)
                    .orElse(null),
            nullsLast(naturalOrder())
    );

    public static final Comparator<MatchEntity> COMPARE_MATCH_ENTITY_BY_NICKNAME2 = Comparator.comparing(
            it -> Optional.ofNullable(it)
                    .map(MatchEntity::getParticipant2)
                    .map(ParticipantEntity::getNickname)
                    .orElse(null),
            nullsLast(naturalOrder())
    );

    public static final Comparator<MatchEntity> COMPARE_MATCH_ENTITY_BY_PLAYER_SCORE_AND_BUCHHOLZ_NULLS_LAST = ComparatorUtil.compareRecursive(
            (m) -> Arrays.asList(m.getParticipant1(), m.getParticipant2()),
            ComparatorUtil.safeCompareByDesc(ParticipantEntity::getScore)
                    .thenComparing(ComparatorUtil.safeCompareByDesc(ParticipantEntity::getBuchholz))
    );

}
