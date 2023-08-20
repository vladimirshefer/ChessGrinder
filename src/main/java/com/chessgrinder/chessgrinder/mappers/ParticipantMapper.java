package com.chessgrinder.chessgrinder.mappers;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class ParticipantMapper {

    private final UserMapper userMapper;

    public List<ParticipantDto> toDto(List<Participant> participants) {

       return participants.stream().map(participant -> ParticipantDto.builder()
                        .user(userMapper.toDto(participant.getUser()))
                        .nickname(participant.getNickname())
                        .buchholz(participant.getBuchholz())
                        .score(participant.getScore()).build())
                .toList();

    }
}
