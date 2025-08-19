package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.TournamentDto;
import com.chessgrinder.chessgrinder.entities.RoundEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.mappers.TournamentMapper;
import com.chessgrinder.chessgrinder.repositories.RoundRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class TournamentService {
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;
    private final List<TournamentListener> tournamentListeners;
    private final TournamentMapper tournamentMapper;
    private final RoundService roundService;
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
    public TournamentDto createTournament(LocalDateTime date, UserEntity owner) {
        TournamentEntity tournamentEntity = TournamentEntity.builder()
                .date(date)
                .status(TournamentStatus.PLANNED)
                .roundsNumber(DEFAULT_ROUNDS_NUMBER)
                .owner(owner)
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
        var tournament = tournamentRepository.findById(tournamentId).orElseThrow();
        var tournamentReopened = TournamentStatus.FINISHED.equals(tournament.getStatus());
        tournament.setStatus(TournamentStatus.ACTIVE);
        TournamentEntity updatedTournament = tournamentRepository.save(tournament);

        if (tournamentReopened) {
            for (TournamentListener tournamentListener : tournamentListeners) {
                try {
                    tournamentListener.tournamentReopened(updatedTournament);
                } catch (Exception e) {
                    log.error("Could not apply tournament listener on tournament {} reopened {}", updatedTournament.getId(), tournamentListener.getClass().getName(), e);
                }
            }
        }
    }

    public void finishTournament(UUID tournamentId) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + tournamentId));

        List<RoundEntity> rounds = roundRepository.findByTournamentId(tournamentId);

        for (RoundEntity round : rounds) {
            if (round.isFinished()) {
                continue;
            }

            if (roundService.isReadyToFinish(round)) {
                roundService.finishRound(tournamentId, round.getNumber());
            } else {
                throw new IllegalStateException("There are open rounds with unknown match results.");
            }
        }

        tournament.setStatus(TournamentStatus.FINISHED);
        TournamentEntity updatedTournament = tournamentRepository.save(tournament);

        for (TournamentListener tournamentListener : tournamentListeners) {
            try {
                tournamentListener.tournamentFinished(updatedTournament);
            } catch (Exception e) {
                log.error("Could not apply tournament listener on tournament finish {}", tournamentListener.getClass().getName(), e);
            }
        }
    }

    public boolean isReadyToFinish(UUID tournamentId) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("Tournament not found with ID: " + tournamentId));

        if (tournament.getRoundsNumber() > tournament.getRounds().size() && tournament.getStatus() == TournamentStatus.ACTIVE) {
            return false;
        }

        List<RoundEntity> rounds = roundRepository.findByTournamentId(tournament.getId());
        if (tournament.getStatus() == TournamentStatus.FINISHED) {
            return true;
        }
        for (RoundEntity round : rounds) {
            if (!roundService.isReadyToFinish(round)) {
                return false;
            }
        }
        return true;
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
        tournament.setPairingStrategy(tournamentDto.getPairingStrategy());
        tournament.setRegistrationLimit(tournamentDto.getRegistrationLimit());
        tournament.setCity(tournamentDto.getCity());
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

    /**
     * Interface to be extended by components. Is used as a callback on tournament state change.
     * Could be used by ratings, achievements,
     */
    public interface TournamentListener {

        /**
         * Do something right after the tournament is marked as finished.
         */
        default void tournamentFinished(TournamentEntity tournamentEntity) {
        }

        /**
         * Do something when already finished tournament was reopened.
         */
        default void tournamentReopened(TournamentEntity tournamentEntity) {

        }

        /**
         * Optional. Rollback all changes done by this listener for all entities.
         * For instance: remove all achievements or reset all ratings.
         * Used when we want to reapply this listener for all data.
         */
        default void totalReset() {

        }

    }

}
