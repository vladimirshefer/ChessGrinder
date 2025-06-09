package com.chessgrinder.chessgrinder.dto;

import java.util.*;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StrawpollRequestDto {

    private String title;

    @JsonProperty("poll_options")
    private List<PollOptionDto> options;
}
