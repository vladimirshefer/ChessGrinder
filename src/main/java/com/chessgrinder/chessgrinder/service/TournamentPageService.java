package com.chessgrinder.chessgrinder.service;

import java.util.*;
import java.util.stream.Collectors;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.dto.pages.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.*;
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
    private final MatchRepository matchRepository;

    private final TournamentMapper tournamentMapper;
    private final ParticipantMapper participantMapper;
    private final RoundMapper roundMapper;

    public TournamentPageDto getTournamentData(UUID tournamentId) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(404, "No tournament with id " + tournamentId, null));
        List<ParticipantEntity> tournamentParticipantEntities = participantRepository.findByTournamentId(tournamentId);
        List<RoundEntity> tournamentRoundEntities = roundRepository.findByTournamentId(tournamentId);

        TournamentDto tournamentDto = tournamentMapper.toDto(tournamentEntity);
        List<RoundDto> roundsDto = roundMapper.toDto(tournamentRoundEntities
                .stream()
                .sorted(Comparator.comparing(RoundEntity::getNumber))
                .collect(Collectors.toList()));

        //TODO сделать сортировку участников по месту в турнире
        List<ParticipantDto> participantsDto = participantMapper.toDto(tournamentParticipantEntities)
                .stream().sorted(Comparator.comparing(ParticipantDto::getPlace))
                .collect(Collectors.toList());

        return TournamentPageDto.builder()
                .tournament(tournamentDto)
                .participants(participantsDto)
                .rounds(roundsDto)
                .build();
    }
}
