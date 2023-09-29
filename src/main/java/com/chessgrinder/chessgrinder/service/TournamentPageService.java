package com.chessgrinder.chessgrinder.service;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.pages.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.mappers.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.web.server.ResponseStatusException;

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
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(404, "No tournament with id " + tournamentId, null));
        List<ParticipantEntity> tournamentParticipantEntities = participantRepository.findByTournamentId(tournamentId);
        List<RoundEntity> tournamentRoundEntities = roundRepository.findByTournamentId(tournamentId);

        return TournamentPageDto.builder()
                .tournament(tournamentMapper.toDto(tournamentEntity))
                .participants(participantMapper.toDto(tournamentParticipantEntities))
                .rounds(roundMapper.toDto(tournamentRoundEntities))
                .build();
    }

}
