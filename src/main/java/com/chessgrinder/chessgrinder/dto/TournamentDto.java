package com.chessgrinder.chessgrinder.dto;

import java.util.*;

import com.chessgrinder.chessgrinder.enums.*;
import lombok.*;

@Data
@Builder
public class TournamentDto {

    private String id;
    private Date date;
    private TournamentStatus status;


}
