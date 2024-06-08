package com.chessgrinder.chessgrinder.dto;

import com.chessgrinder.chessgrinder.utils.Const;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SubscriptionDto {
    private String id;
    private UserDto user;
    private ClubDto club;
    private SubscriptionLevelDto subscriptionLevel;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Const.Tournaments.DATETIME_PATTERN)
    private Instant startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Const.Tournaments.DATETIME_PATTERN)
    private Instant finishDate;
}
