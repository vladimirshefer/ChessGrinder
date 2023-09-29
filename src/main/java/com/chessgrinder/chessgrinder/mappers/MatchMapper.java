package com.chessgrinder.chessgrinder.mappers;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class MatchMapper {

    private final UserMapper userMapper;

    public MatchDto toDto(MatchEntity matchEntity) {

        return MatchDto.builder()
                .id(matchEntity.getId().toString())
                .white(userMapper.toDto(matchEntity.getParticipant1().getUser()))
                .black(userMapper.toDto(matchEntity.getParticipant2().getUser()))
                .result(matchEntity.getResult())
                .build();
    }

    public List<MatchDto> toDto(List<MatchEntity> matchEntities) {
        return matchEntities.stream().map(this::toDto).toList();
    }
}
