package com.chessgrinder.chessgrinder.dto;

import com.chessgrinder.chessgrinder.utils.Const;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ClubDto {
    private String id;
    private String name;
    private String description;
    private String location;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Const.Tournaments.DATETIME_PATTERN)
    private Instant registrationDate;
}
