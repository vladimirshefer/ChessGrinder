package com.chessgrinder.chessgrinder.mappers;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class ParticipantMapper {

    private final TournamentMapper tournamentMapper;
    public ParticipantDto toDto(ParticipantEntity participant) {
        return ParticipantDto.builder()
                .id(participant.getId().toString())
                .userId(Optional.ofNullable(participant.getUser()).map(it -> it.getId().toString()).orElse(null))
                .name(participant.getNickname())
                        .tournament(tournamentMapper.toDto(participant.getTournament()))
                .buchholz(participant.getBuchholz())
                .score(participant.getScore())
                .build();
    }

    public List<ParticipantDto> toDto(List<ParticipantEntity> participantEntities) {
       return participantEntities.stream().map(this::toDto).toList();
    }
}
