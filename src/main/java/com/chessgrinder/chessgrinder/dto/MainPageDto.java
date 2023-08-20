package com.chessgrinder.chessgrinder.dto;

import java.util.*;

import lombok.*;

@Data
@Builder
public class MainPageDto {

    private List<UserDto> users;
    private List<TournamentDto> tournaments;
}
