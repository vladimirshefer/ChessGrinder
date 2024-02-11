package com.chessgrinder.chessgrinder.service;

import java.math.*;
import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.mappers.ParticipantMapper;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final TournamentRepository tournamentRepository;
    private final ParticipantMapper participantMapper;
    private final UserRepository userRepository;

    public void addParticipantToTheTournament(UUID tournamentId, ParticipantDto participantDto) {

        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentId).orElseThrow(); //TODO Exception
        UserEntity userEntity = null;
        if (participantDto.getUserId() != null) {
            userEntity = userRepository.findById(UUID.fromString(participantDto.getUserId())).orElse(null);
        }

        ParticipantEntity participantEntity = ParticipantEntity.builder()
                .tournament(tournamentEntity)
                .nickname(participantDto.getName())
                .user(userEntity)
                .score(BigDecimal.ZERO)
                .buchholz(BigDecimal.ZERO)
                .place(-1)
                .build();

        participantRepository.save(participantEntity);
    }

    public void delete(UUID participantId) {
        ParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new NoSuchElementException("No participant with id " + participantId));
        if (participant != null) {
            participantRepository.delete(participant);
        }
    }

    public ParticipantDto get(UUID participantId) {
        ParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new NoSuchElementException("No participant with id " + participantId));
        return participantMapper.toDto(participant);
    }

}
