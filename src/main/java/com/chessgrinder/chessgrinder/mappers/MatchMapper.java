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
                .user1(userMapper.toDto(match.getUser1()))
                .user1(userMapper.toDto(match.getUser2()))
                .result(match.getResult())
                .build();
    }

    public List<MatchDto> toDto(List<Match> matches) {

        return matches.stream().map(match -> MatchDto.builder()
                .user1(userMapper.toDto(match.getUser1()))
                .user2(userMapper.toDto(match.getUser1()))
                .result(match.getResult())
                .build())
            .toList();
    }
}
