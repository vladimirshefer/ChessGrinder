package com.chessgrinder.chessgrinder.dto;

import java.time.*;
import java.util.*;

import com.chessgrinder.chessgrinder.enums.*;
import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Data
@Builder
public class TournamentDto {

    private String id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime date;

    private TournamentStatus status;


}
