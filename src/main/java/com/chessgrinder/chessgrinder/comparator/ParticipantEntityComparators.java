package com.chessgrinder.chessgrinder.comparator;

import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Comparator;

import static java.util.Comparator.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParticipantEntityComparators {

    public static final Comparator<ParticipantEntity> COMPARE_PARTICIPANT_ENTITY_BY_SCORE_NULLS_LAST = nullsLast( // If participant is null then low precedence
            Comparator.comparing(
                    ParticipantEntity::getScore,
                    nullsLast(reverseOrder()) // if the score is null (should not happen) then participant is low precedence
            )
    );

    public static final Comparator<ParticipantEntity> COMPARE_PARTICIPANT_ENTITY_BY_BUCHHOLZ_NULLSLAST = nullsLast( // If participant is null then low precedence
            Comparator.comparing(
                    ParticipantEntity::getBuchholz,
                    nullsLast(reverseOrder()) // if the buchholz is null (should not happen) then participant is low precedence
            )
    );
}
