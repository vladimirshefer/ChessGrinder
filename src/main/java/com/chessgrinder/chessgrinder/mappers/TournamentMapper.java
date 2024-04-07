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
                .name(tournamentEntity.getName())
                .locationName(tournamentEntity.getLocationName())
                .locationUrl(tournamentEntity.getLocationUrl())
                .date(tournamentEntity.getDate())
                .status(tournamentEntity.getStatus())
                .numberOfRounds(tournamentEntity.getNumberOfRounds())
                .build();
    }

    public List<TournamentDto> toDto(List<TournamentEntity> tournamentEntities) {
        return tournamentEntities.stream().map(this::toDto).toList();
    }
}
