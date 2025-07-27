package com.chessgrinder.chessgrinder.mappers;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class TournamentEventMapper {

    private final TournamentMapper tournamentMapper;
    private final ParticipantMapper participantMapper;
    private final ParticipantRepository participantRepository;

    public TournamentEventDto toDto(TournamentEventEntity eventEntity) {
        return TournamentEventDto.builder()
                .id(eventEntity.getId().toString())
                .name(eventEntity.getName())
                .locationName(eventEntity.getLocationName())
                .locationUrl(eventEntity.getLocationUrl())
                .date(eventEntity.getDate())
                .status(eventEntity.getStatus())
                .roundsNumber(eventEntity.getRoundsNumber())
                .registrationLimit(eventEntity.getRegistrationLimit())
                .tournaments(tournamentMapper.toDto(eventEntity.getTournaments()))
                .participants(participantMapper.toDto(participantRepository.findByEventId(eventEntity.getId())))
                .build();
    }

    public List<TournamentEventDto> toDto(List<TournamentEventEntity> eventEntities) {
        return eventEntities.stream().map(this::toDto).toList();
    }
}