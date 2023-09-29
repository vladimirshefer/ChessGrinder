package com.chessgrinder.chessgrinder.mappers;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class TournamentMapper {

    public TournamentDto toDto(TournamentEntity tournamentEntity) {
        return TournamentDto.builder()
                .id(tournamentEntity.getId().toString())
                .date(tournamentEntity.getDate())
                .status(tournamentEntity.getStatus())
                .build();
    }

    public List<TournamentDto> toDto(List<TournamentEntity> tournamentEntities) {
        return tournamentEntities.stream().map(tournament -> TournamentDto.builder()
                .id(tournament.getId().toString())
                .date(tournament.getDate())
                .status(tournament.getStatus())
                .build())
            .toList();
    }


}
