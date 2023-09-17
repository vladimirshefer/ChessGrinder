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

        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(); //TODO Exception
        User user = userRepository.findById(UUID.fromString(participantDto.getUserId())).orElse(null);


        Participant participant = Participant.builder()
                .id(UUID.randomUUID())
                .tournament(tournament)
                .nickname(participantDto.getName())
                .user(user)
                .score(BigDecimal.ZERO)
                .buchholz(BigDecimal.ZERO)
                .build();

        participantRepository.save(participant);
    }
}
