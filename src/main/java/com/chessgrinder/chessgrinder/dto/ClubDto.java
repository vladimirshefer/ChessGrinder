package com.chessgrinder.chessgrinder.dto;

import com.chessgrinder.chessgrinder.ApplicationConstants;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ApplicationConstants.DATETIME_FORMAT_STR)
    private Instant registrationDate;
}
