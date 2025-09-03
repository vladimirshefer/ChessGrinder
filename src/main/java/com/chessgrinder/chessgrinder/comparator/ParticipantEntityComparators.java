package com.chessgrinder.chessgrinder.comparator;

import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Comparator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParticipantEntityComparators {

    public static final Comparator<ParticipantEntity> COMPARE_PARTICIPANT_ENTITY_BY_NICKNAME_NULLS_LAST =
            ComparatorUtil.safeCompareByAsc(ParticipantEntity::getNickname);

    public static final Comparator<ParticipantEntity> COMPARE_PARTICIPANT_ENTITY_BY_SCORE_NULLS_LAST =
            ComparatorUtil.safeCompareByDesc(ParticipantEntity::getScore);

    public static final Comparator<ParticipantEntity> COMPARE_PARTICIPANT_ENTITY_BY_BUCHHOLZ_NULLSLAST =
            ComparatorUtil.safeCompareByDesc(ParticipantEntity::getBuchholz);
}
