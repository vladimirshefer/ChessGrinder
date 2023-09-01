package com.chessgrinder.chessgrinder.dto;

import java.time.*;
import java.util.*;

import com.chessgrinder.chessgrinder.enums.*;
import lombok.*;

@Data
@Builder
public class TournamentDto {

    private String id;
    private LocalDateTime date;
    private TournamentStatus status;


}
