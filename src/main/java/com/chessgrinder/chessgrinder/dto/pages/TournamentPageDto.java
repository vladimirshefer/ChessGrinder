package com.chessgrinder.chessgrinder.dto.pages;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import lombok.*;

@Data
@Builder
public class TournamentPageDto {

    private TournamentDto tournament;
    private List<ParticipantDto> participants;
    private List<RoundDto> rounds;
}
