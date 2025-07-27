package com.chessgrinder.chessgrinder.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import static com.chessgrinder.chessgrinder.dto.TournamentDto.API_DATETIME_FORMAT;

public interface EventData {
    String getId();
    String getName();
    String getLocationName();
    String getLocationUrl();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = API_DATETIME_FORMAT)
    LocalDateTime getDate();
    Integer getRoundsNumber();
    Integer getRegistrationLimit();
}
