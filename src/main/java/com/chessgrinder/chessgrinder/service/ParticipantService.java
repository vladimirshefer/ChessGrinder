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
    public void addParticipantToTheTournament(UUID tournamentId, ParticipantDto participantDto) {

        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(); //TODO Exception

        Participant participant = Participant.builder()
                .id(UUID.randomUUID())
                .tournament(tournament)
                .nickname(participantDto.getName())
                .score(BigDecimal.ZERO)
                .buchholz(BigDecimal.ZERO)
                .build();

        participantRepository.save(participant);
    }
}
