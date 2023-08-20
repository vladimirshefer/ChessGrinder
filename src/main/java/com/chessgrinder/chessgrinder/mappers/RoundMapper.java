package com.chessgrinder.chessgrinder.mappers;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class RoundMapper {

    private final MatchMapper matchMapper;

    public List<RoundDto> toDto(List<Round> rounds) {

        return rounds.stream().map(round -> RoundDto.builder()
                .isFinished(round.isFinished())
                .number(round.getNumber())
                .matches(matchMapper.toDto(round.getMatches()))
                .build())
            .toList();
    }

    public RoundDto toDto(Round round) {

        return RoundDto.builder()
                .isFinished(round.isFinished())
                .number(round.getNumber())
                .build();
    }
}
