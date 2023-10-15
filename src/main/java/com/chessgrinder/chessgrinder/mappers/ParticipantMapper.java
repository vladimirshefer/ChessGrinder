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

    private final TournamentMapper tournamentMapper;
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

    public ParticipantEntity toEntity(ParticipantDto participantDto) {

        UserEntity user = null;
        if (participantDto.getUserId() != null) {
            user = userRepository.findById(UUID.fromString(participantDto.getUserId())).orElse(null);
        }
        return ParticipantEntity.builder()
                .user(user)
                .buchholz(participantDto.getBuchholz())
                .score(participantDto.getScore())
                .nickname(participantDto.getName())
                .build();
    }

    public List<ParticipantEntity> toEntity(List<ParticipantDto> participantsDto) {
        return participantsDto.stream().map(this::toEntity).toList();
    }
}
