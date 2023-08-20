package com.chessgrinder.chessgrinder.service;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.pages.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.mappers.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class TournamentPageService {

    private final TournamentRepository tournamentRepository;
    private final ParticipantRepository participantRepository;
    private final RoundRepository roundRepository;
    private final TournamentMapper tournamentMapper;
    private final ParticipantMapper participantMapper;
    private final RoundMapper roundMapper;

    public TournamentPageDto getTournamentData(UUID tournamentId) {


        Tournament tournament = tournamentRepository.findById(tournamentId).orElse(null);
        List<Participant> tournamentParticipants = participantRepository.findByTournamentId(tournamentId);
        List<Round> tournamentRounds = roundRepository.findByTournamentId(tournamentId);

        TournamentPageDto tournamentDto = TournamentPageDto.builder()
                .tournament(tournamentMapper.toDto(tournament))
                .participants(participantMapper.toDto(tournamentParticipants))
                .rounds(roundMapper.toDto(tournamentRounds))
                .build();

        return tournamentDto;
    }

}
