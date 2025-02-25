package com.chessgrinder.chessgrinder.mappers;

import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ParticipantMapper {

    private final TournamentMapper tournamentMapper;

    public ParticipantDto toDto(ParticipantEntity participant) {
        if (participant == null) return null;

        return ParticipantDto.builder()
                .id(participant.getId().toString())
                .name(participant.getNickname())
                .userId(Optional.ofNullable(participant.getUser()).map(it -> it.getId().toString()).orElse(null))
                .userFullName(Optional.ofNullable(participant.getUser()).map(UserEntity::getName).orElse(null))
                .buchholz(participant.getBuchholz())
                .score(participant.getScore())
                .isMissing(participant.isMissing())
                .isModerator(participant.isModerator())
                .place(participant.getPlace())
                .tournament(Optional.ofNullable(participant.getTournament()).map(tournamentMapper::toDto).orElse(null))
                .initialElo(participant.getInitialEloPoints())
                .finalElo(participant.getFinalEloPoints())
                .build();
    }

    public List<ParticipantDto> toDto(List<ParticipantEntity> participantEntities) {
       return participantEntities.stream().map(this::toDto).toList();
    }
}
