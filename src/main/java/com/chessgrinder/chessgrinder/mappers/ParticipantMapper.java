package com.chessgrinder.chessgrinder.mappers;

import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ParticipantMapper {

    private final TournamentRepository tournamentRepository;
    private final UserRepository userRepository;

    public ParticipantDto toDto(ParticipantEntity participant) {
        if (participant == null) return null;

        return ParticipantDto.builder()
                .id(participant.getId().toString())
                .name(participant.getNickname())
                .userId(Optional.ofNullable(participant.getUser()).map(it -> it.getId().toString()).orElse(null))
                .buchholz(participant.getBuchholz())
                .score(participant.getScore())
                .build();
    }

    public List<ParticipantDto> toDto(List<ParticipantEntity> participantEntities) {
       return participantEntities.stream().map(this::toDto).toList();
    }
}
