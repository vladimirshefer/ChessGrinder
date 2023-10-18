package com.chessgrinder.chessgrinder.service;

import java.util.*;

import com.chessgrinder.chessgrinder.chessengine.*;
import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.exceptions.*;
import com.chessgrinder.chessgrinder.mappers.*;
import com.chessgrinder.chessgrinder.repositories.*;
import jakarta.annotation.Nullable;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoundService {
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;
    private final ParticipantRepository participantRepository;
    private final SwissMatchupStrategyImpl swissEngine;
    private final MatchRepository matchRepository;

    private final MatchMapper matchMapper;

    private final ParticipantMapper participantMapper;

    public void createRound(UUID tournamentId) {

        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentId).orElseThrow();
        RoundEntity lastExistedRoundEntity = roundRepository.findFirstByTournamentIdOrderByNumberDesc(tournamentId);
        Integer nextRoundNumber = lastExistedRoundEntity != null ? lastExistedRoundEntity.getNumber() + 1 : 1;

        RoundEntity nextRoundEntity = RoundEntity.builder()
                .id(UUID.randomUUID())
                .number(nextRoundNumber)
                .tournament(tournamentEntity)
                .matches(List.of())
                .isFinished(false)
                .build();

        //TODO do not create round if the tournament is finished

        roundRepository.save(nextRoundEntity);
    }

    public void finishRound(UUID tournamentId, Integer roundNumber) {
        RoundEntity roundEntity = roundRepository.findByTournamentIdAndNumber(tournamentId, roundNumber);
        if (roundEntity != null) {
            roundEntity.setFinished(true);
            roundRepository.save(roundEntity);
        }
    }

    public void reopenRound(UUID tournamentId, Integer roundNumber) {
        RoundEntity roundEntity = roundRepository.findByTournamentIdAndNumber(tournamentId, roundNumber);
        if (roundEntity != null) {
            roundEntity.setFinished(false);
            roundRepository.save(roundEntity);
        }
    }

    public void makeMatchUp(UUID tournamentId, Integer roundNumber) {

        RoundEntity round = roundRepository.findByTournamentIdAndNumber(tournamentId, roundNumber);

        if (round.isDrawed()) {
            List<MatchEntity> alreadyExistedMatches = matchRepository.findMatchEntitiesByRoundId(round.getId());
            matchRepository.deleteAll(alreadyExistedMatches);
        }

        List<ParticipantEntity> participantEntities = participantRepository.findByTournamentId(tournamentId);
        List<ParticipantDto> participantDtos = participantMapper.toDto(participantEntities);

        List<MatchEntity> allMatchesInTheTournament = matchRepository.findAllByTournamentId(tournamentId);
        List<MatchDto> allMatches = matchMapper.toDto(allMatchesInTheTournament);

        List<MatchDto> matchesDto = swissEngine.matchUp(participantDtos, allMatches);
        List<MatchEntity> matches = new ArrayList<>();

        for (MatchDto matchDto: matchesDto) {

            ParticipantEntity participant1 = null;
            ParticipantEntity participant2 = null;

            if (matchDto.getWhite() != null) {
                participant1 = participantRepository.findById(UUID.fromString(matchDto.getWhite().getId())).orElse(null);
            }
            if (matchDto.getBlack() != null) {
                participant2 = participantRepository.findById(UUID.fromString(matchDto.getBlack().getId())).orElse(null);
            }

            matches.add(
                    MatchEntity.builder()
                            .round(round)
                            .participant1(participant1)
                            .participant2(participant2)
                            .result(matchDto.getResult())
                            .build());
        }

        matchRepository.saveAll(matches);

        round.setDrawed(true);
        roundRepository.save(round);
    }

    @Nullable
    private ParticipantEntity getParticipantEntity(@Nullable ParticipantDto participantDto) {
        if (participantDto == null) return null;
        return participantRepository.findById(UUID.fromString(participantDto.getId())).get();
    }

    public void markUserAsMissedInTournament(UUID userId, UUID tournamentId) {
        ParticipantEntity participantEntity = participantRepository.findByTournamentIdAndUserId(tournamentId, userId);
        participantEntity.setMissing(true);
        participantRepository.save(participantEntity);
    }

    public void deleteRound(UUID tournamentId, Integer roundNumber) throws RoundNotFoundException {

        RoundEntity roundEntity = roundRepository.findByTournamentIdAndNumber(tournamentId, roundNumber);

        if (roundEntity != null) {
            roundRepository.delete(roundEntity);
        } else {
            log.error("There is no round with number: " + roundNumber + " in the tournament with id: " + tournamentId);
            throw new RoundNotFoundException();
        }
    }
}
