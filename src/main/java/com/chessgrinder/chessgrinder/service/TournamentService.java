package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.TournamentDto;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.mappers.TournamentMapper;
import com.chessgrinder.chessgrinder.repositories.MatchRepository;
import com.chessgrinder.chessgrinder.repositories.RoundRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import jakarta.annotation.Nullable;
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
    private final MatchRepository matchRepository;
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

        List<RoundEntity> rounds = roundRepository.findByTournamentId(tournamentId);
        boolean allRoundsFinished = true;

        for (RoundEntity round : rounds) {
            if (!round.isFinished()) {
                boolean allMatchesHaveResults = round.getMatches().stream()
                        .allMatch(match -> match.getResult() != null);


                if (allMatchesHaveResults) {
                    round.setFinished(true);
                    roundRepository.save(round);
                }
                else {
                    allRoundsFinished = false;
                    break;
                }
            }
        }

        if (!allRoundsFinished) {

            throw new IllegalStateException("There are open rounds with unknown match results.");
        }

        try {
            roundService.updateResults(tournamentId);
        } catch (Exception e) {
            log.error("Could not update results", e);
        }

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
        tournament.setPairingStrategy(tournamentDto.getPairingStrategy());
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

    @Transactional
    public void submitMyResult(TournamentEntity tournament, UserEntity user, MatchResult matchResult) {
        if (matchResult == MatchResult.BUY || matchResult == MatchResult.MISS) {
            throw new IllegalArgumentException("Users are not allowed to set BYE and MISS results");
        }
        RoundEntity activeRound = tournament.getRounds().stream()
                .filter(it -> !it.isFinished())
                .max(Comparator.comparing(RoundEntity::getNumber, Comparator.nullsFirst(Comparator.naturalOrder())))
                .orElseThrow(() -> new IllegalArgumentException("Tournament has no active rounds"));
        MatchEntity match = activeRound.getMatches().stream()
                .filter(it -> belongs(user, it.getParticipant1()) || belongs(user, it.getParticipant2()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("User does not participate in round"));
        match.setResult(matchResult);
        matchRepository.save(match);
    }

    private static boolean belongs(@Nullable UserEntity user, @Nullable ParticipantEntity participant) {
        if (user == null || participant == null || user.getId() == null ||
                participant.getUser() == null || participant.getUser().getId() == null) {
            return false;
        }

        return participant.getUser().getId().equals(user.getId());
    }

}
