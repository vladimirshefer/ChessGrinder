package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.TournamentDto;
import com.chessgrinder.chessgrinder.entities.Round;
import com.chessgrinder.chessgrinder.entities.Tournament;
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

    private final SwissService swissService;
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;
    private final TournamentMapper tournamentMapper;

    public List<TournamentDto> findTournaments() {
        return tournamentRepository.findAll().stream().map(tournamentMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public void createTournament(LocalDateTime date) {


        Tournament tournament = Tournament.builder()
                .id(UUID.randomUUID())
                .date(date)
                .status(TournamentStatus.PLANNED)
                .build();

        tournament = tournamentRepository.save(tournament);

        Round firstRound = Round.builder()
                .id(UUID.randomUUID())
                .tournament(tournament)
                .matches(List.of())
                .number(1)
                .isFinished(false)
                .build();

        roundRepository.save(firstRound);
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


}
