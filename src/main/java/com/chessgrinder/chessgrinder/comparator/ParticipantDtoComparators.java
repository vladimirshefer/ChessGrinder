package com.chessgrinder.chessgrinder.comparator;

import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Comparator;

import static java.util.Comparator.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParticipantDtoComparators {

    public static final Comparator<ParticipantDto> COMPARE_PARTICIPANT_DTO_BY_NICKNAME_NULLS_LAST = nullsLast(comparing(ParticipantDto::getName, nullsLast(naturalOrder())));

    public static final Comparator<ParticipantDto> COMPARE_PARTICIPANT_DTO_BY_BEST = comparing(ParticipantDto::getScore, nullsLast(reverseOrder()))
            .thenComparing(ParticipantDto::getBuchholz, nullsLast(reverseOrder()))
            .thenComparing(ParticipantDto::getName, nullsLast(naturalOrder()))
            .thenComparing(nullsLast(comparing(ParticipantDto::getUserId, nullsLast(naturalOrder()))));

}
