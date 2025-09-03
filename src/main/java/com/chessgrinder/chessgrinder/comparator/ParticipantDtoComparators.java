package com.chessgrinder.chessgrinder.comparator;

import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Comparator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParticipantDtoComparators {

    public static final Comparator<ParticipantDto> COMPARE_PARTICIPANT_DTO_BY_NICKNAME_NULLS_LAST =
            ComparatorUtil.safeCompareByAsc(ParticipantDto::getName);

}
