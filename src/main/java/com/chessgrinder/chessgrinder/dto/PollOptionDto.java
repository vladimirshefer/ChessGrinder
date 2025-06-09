package com.chessgrinder.chessgrinder.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PollOptionDto {

    @JsonProperty("value")
    private String value;
}
