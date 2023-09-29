package com.chessgrinder.chessgrinder.mappers;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class ParticipantMapper {
    public List<ParticipantDto> toDto(List<ParticipantEntity> participantEntities) {

       return participantEntities.stream().map(participant -> ParticipantDto.builder()
                        .userId(participant.getUser().getId().toString())
                        .name(participant.getNickname())
                        .buchholz(participant.getBuchholz())
                        .score(participant.getScore()).build())
                .toList();
    }
}
