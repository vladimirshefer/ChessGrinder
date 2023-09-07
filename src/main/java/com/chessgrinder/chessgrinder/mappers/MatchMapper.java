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

    public MatchDto toDto(Match match) {

        return MatchDto.builder()
                .id(match.getId().toString())
                .white(userMapper.toDto(match.getUser1()))
                .white(userMapper.toDto(match.getUser2()))
                .result(match.getResult())
                .build();
    }

    public List<MatchDto> toDto(List<Match> matches) {
        return matches.stream().map(this::toDto).toList();
    }
}
