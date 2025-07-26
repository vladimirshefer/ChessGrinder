package com.chessgrinder.chessgrinder.mappers;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class TournamentEventScheduleMapper {

    private final TournamentEventMapper tournamentEventMapper;

    public TournamentEventScheduleDto toDto(TournamentEventScheduleEntity scheduleEntity) {
        if (scheduleEntity == null) {
            return null;
        }
        
        return TournamentEventScheduleDto.builder()
                .id(scheduleEntity.getId().toString())
                .name(scheduleEntity.getName())
                .dayOfWeek(scheduleEntity.getDayOfWeek())
                .time(scheduleEntity.getTime())
                .status(scheduleEntity.getStatus())
                .events(scheduleEntity.getEvents() != null ? 
                        tournamentEventMapper.toDto(scheduleEntity.getEvents()) : null)
                .build();
    }

    public List<TournamentEventScheduleDto> toDto(List<TournamentEventScheduleEntity> scheduleEntities) {
        if (scheduleEntities == null) {
            return Collections.emptyList();
        }
        return scheduleEntities.stream().map(this::toDto).toList();
    }
}