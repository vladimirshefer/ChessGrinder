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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;
    private final TournamentMapper tournamentMapper;

    public List<TournamentDto> findTournaments() {
        return tournamentRepository.findAll().stream().map(tournamentMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public TournamentDto createTournament(LocalDateTime date) {
        TournamentEntity tournamentEntity = TournamentEntity.builder()
                .date(date)
                .status(TournamentStatus.PLANNED)
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
}
