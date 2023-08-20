package com.chessgrinder.chessgrinder.mappers;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class TournamentMapper {

    public TournamentDto toDto(Tournament tournament) {
        return TournamentDto.builder()
                .id(tournament.getId().toString())
                .date(tournament.getDate())
                .status(tournament.getStatus())
                .build();
    }

    public List<TournamentDto> toDto(List<Tournament> tournaments) {
        return tournaments.stream().map(tournament -> TournamentDto.builder()
                .id(tournament.getId().toString())
                .date(tournament.getDate())
                .status(tournament.getStatus())
                .build())
            .toList();
    }


}
