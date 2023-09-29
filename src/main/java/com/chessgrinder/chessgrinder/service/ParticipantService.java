package com.chessgrinder.chessgrinder.service;

import java.math.*;
import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final TournamentRepository tournamentRepository;

    private final UserRepository userRepository;
    public void addParticipantToTheTournament(UUID tournamentId, ParticipantDto participantDto) {

        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentId).orElseThrow(); //TODO Exception
        UserEntity userEntity = userRepository.findById(UUID.fromString(participantDto.getUserId())).orElse(null);


        ParticipantEntity participantEntity = ParticipantEntity.builder()
                .id(UUID.randomUUID())
                .tournamentEntity(tournamentEntity)
                .nickname(participantDto.getName())
                .userEntity(userEntity)
                .score(BigDecimal.ZERO)
                .buchholz(BigDecimal.ZERO)
                .build();

        participantRepository.save(participantEntity);
    }
}
