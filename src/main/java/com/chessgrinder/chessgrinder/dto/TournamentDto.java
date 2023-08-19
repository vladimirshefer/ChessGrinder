package com.chessgrinder.chessgrinder.dto;

import java.util.*;

import com.chessgrinder.chessgrinder.enums.*;
import lombok.*;

@Data
public class TournamentDto {

    private Date date;
    private TournamentStatus status;
}
