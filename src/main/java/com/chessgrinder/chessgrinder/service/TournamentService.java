package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.TournamentDto;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.mappers.TournamentMapper;
import com.chessgrinder.chessgrinder.repositories.RoundRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;
    private final TournamentMapper tournamentMapper;
    private static final int DEFAULT_ROUNDS_NUMBER = 6;
    private static final int MIN_ROUNDS_NUMBER = 0;
    private static final int MAX_ROUNDS_NUMBER = 99;

    public List<TournamentDto> findTournaments() {
        return tournamentRepository.findAll().stream()
                .sorted(
                        Comparator.comparing(TournamentEntity::getDate).reversed()
                                .thenComparing(TournamentEntity::getId)
                )
                .map(tournamentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TournamentDto createTournament(LocalDateTime date) {
        TournamentEntity tournamentEntity = TournamentEntity.builder()
                .date(date)
                .status(TournamentStatus.PLANNED)
                .roundsNumber(DEFAULT_ROUNDS_NUMBER)
                .build();

        tournamentEntity = tournamentRepository.save(tournamentEntity);

        RoundEntity firstRoundEntity = RoundEntity.builder()
                .tournament(tournamentEntity)
                .matches(List.of())
                .number(1)
                .isFinished(false)
                .build();

        roundRepository.save(firstRoundEntity);
        return tournamentMapper.toDto(tournamentEntity);
    }

    public void startTournament(UUID tournamentId) {
        tournamentRepository.findById(tournamentId).ifPresent(tournament -> {
            tournament.setStatus(TournamentStatus.ACTIVE);
            tournamentRepository.save(tournament);
        });
    }

    public void finishTournament(UUID tournamentId) {
        tournamentRepository.findById(tournamentId).ifPresent(tournament -> {
            tournament.setStatus(TournamentStatus.FINISHED);
            tournamentRepository.save(tournament);
        });
    }

    public void deleteTournament(UUID tournamentId) {
        tournamentRepository.deleteById(tournamentId);
    }

    public void updateTournament(UUID tournamentId, TournamentDto tournamentDto) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(404, "No tournament with id " + tournamentId, null));
        tournament.setName(tournamentDto.getName());
        tournament.setDate(tournamentDto.getDate());
        tournament.setLocationName(tournamentDto.getLocationName());
        tournament.setLocationUrl(tournamentDto.getLocationUrl());
        final var roundsNum = tournamentDto.getRoundsNumber();
        if (roundsNum < MIN_ROUNDS_NUMBER || roundsNum > MAX_ROUNDS_NUMBER) {
            throw new ResponseStatusException(400, "Wrong rounds number range", null);
        }
        if (roundsNum < tournament.getRounds().size()) {
            throw new ResponseStatusException(400, "Entered rounds number is less than existing rounds number", null);
        }
        tournament.setRoundsNumber(roundsNum);
        tournamentRepository.save(tournament);
    }
}
