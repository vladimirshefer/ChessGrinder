package com.chessgrinder.chessgrinder.dto;

import com.chessgrinder.chessgrinder.ApplicationConstants;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ApplicationConstants.DATETIME_FORMAT_STR)
    private Instant startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ApplicationConstants.DATETIME_FORMAT_STR)
    private Instant finishDate;
}
