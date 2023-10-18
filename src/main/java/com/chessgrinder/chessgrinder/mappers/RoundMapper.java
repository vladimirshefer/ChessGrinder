package com.chessgrinder.chessgrinder.mappers;

import java.util.*;
import java.util.stream.Collectors;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class RoundMapper {

    private final MatchMapper matchMapper;

    public List<RoundDto> toDto(List<RoundEntity> roundEntities) {

        return roundEntities.stream().map(round -> RoundDto.builder()
                        .isFinished(round.isFinished())
                        .number(round.getNumber())
                        .matches(matchMapper.toDto(round.getMatches()
                                .stream().sorted(
                                        Comparator.nullsLast(Comparator
                                                        .<MatchEntity, String>comparing(
                                                                it -> Optional.ofNullable(it)
                                                                        .map(MatchEntity::getParticipant1)
                                                                        .map(ParticipantEntity::getNickname)
                                                                        .orElse(null)
                                                        ))
                                                .thenComparing(Comparator.nullsLast(Comparator
                                                        .comparing(
                                                                it -> Optional.ofNullable(it)
                                                                        .map(MatchEntity::getParticipant2)
                                                                        .map(ParticipantEntity::getNickname)
                                                                        .orElse(null)
                                                        ))
                                                )
                                )
                                .collect(Collectors.toList())
                        ))
                        .build())
                .toList();
    }

    public RoundDto toDto(RoundEntity roundEntity) {
        return RoundDto.builder()
                .isFinished(roundEntity.isFinished())
                .number(roundEntity.getNumber())
                .build();
    }
}
