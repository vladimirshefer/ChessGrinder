package com.chessgrinder.chessgrinder.dto.pages;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import lombok.*;

@Data
@Builder
public class MainPageDto {

    private List<UserDto> users;
    private List<TournamentDto> tournaments;
}
