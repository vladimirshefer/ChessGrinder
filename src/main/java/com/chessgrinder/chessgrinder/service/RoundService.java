package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.chessengine.PairingStrategy;
import com.chessgrinder.chessgrinder.dto.MatchDto;
import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoundEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.exceptions.RoundNotFoundException;
import com.chessgrinder.chessgrinder.mappers.MatchMapper;
import com.chessgrinder.chessgrinder.mappers.ParticipantMapper;
import com.chessgrinder.chessgrinder.repositories.MatchRepository;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.RoundRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoundService {
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;
    private final ParticipantRepository participantRepository;
    private final PairingStrategy swissEngine;
    private final MatchRepository matchRepository;

    private final MatchMapper matchMapper;

    private final ParticipantMapper participantMapper;

    public void createRound(UUID tournamentId) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentId).orElseThrow();
        RoundEntity lastExistedRoundEntity = roundRepository.findFirstByTournamentIdOrderByNumberDesc(tournamentId);
        Integer nextRoundNumber = lastExistedRoundEntity != null ? lastExistedRoundEntity.getNumber() + 1 : 1;
        if (nextRoundNumber > tournamentEntity.getNumberOfRounds()) {
            throw new ResponseStatusException(400, "Can't add new round. Change number of rounds in settings", null);
        }

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
        if (roundEntity == null) {
            return;
        }
        for (MatchEntity match : roundEntity.getMatches()) {

            if (match.getResult() == null) {
                throw new IllegalStateException("Can not finish round with unknown match result");

            }
        }
        roundEntity.setFinished(true);
        roundRepository.save(roundEntity);
        try {
            updateResults(tournamentId);
        } catch (Exception e) {
            log.error("Could not update results", e);
        }
    }

    public void reopenRound(UUID tournamentId, Integer roundNumber) {
        RoundEntity roundEntity = roundRepository.findByTournamentIdAndNumber(tournamentId, roundNumber);
        if (roundEntity == null) {
            return;
        }
        roundEntity.setFinished(false);
        roundRepository.save(roundEntity);
        try {
            updateResults(tournamentId);
        } catch (Exception e) {
            log.error("Could not update results", e);
        }
    }

    @Transactional
    public void makePairings(UUID tournamentId, Integer roundNumber) {

        RoundEntity round = roundRepository.findByTournamentIdAndNumber(tournamentId, roundNumber);

        List<MatchEntity> alreadyExistedMatches = matchRepository.findMatchEntitiesByRoundId(round.getId());
        matchRepository.deleteAll(alreadyExistedMatches);

        List<ParticipantEntity> participantEntities = participantRepository.findByTournamentId(tournamentId);
        List<ParticipantDto> participantDtos = participantMapper.toDto(participantEntities);

        TournamentEntity tournament = tournamentRepository.findById(tournamentId).get();

        List<List<MatchEntity>> allMatchesInTheTournament = tournament.getRounds().stream()
                .filter(RoundEntity::isFinished)
                .map(RoundEntity::getMatches)
                .toList();

        List<List<MatchDto>> allMatches = allMatchesInTheTournament.stream().map(matchMapper::toDto).toList();

        List<MatchDto> matchesDto = swissEngine.makePairings(participantDtos, allMatches, tournament.getNumberOfRounds(), false);
        List<MatchEntity> matches = new ArrayList<>();

        for (MatchDto matchDto : matchesDto) {

            ParticipantEntity participant1 = null;
            ParticipantEntity participant2 = null;

            if (matchDto.getWhite() != null) {
                participant1 = participantRepository.findById(UUID.fromString(matchDto.getWhite().getId())).orElse(null);
            }
            if (matchDto.getBlack() != null) {
                participant2 = participantRepository.findById(UUID.fromString(matchDto.getBlack().getId())).orElse(null);
            }

            matches.add(MatchEntity.builder()
                    .round(round)
                    .participant1(participant1)
                    .participant2(participant2)
                    .result(matchDto.getResult())
                    .build()
            );
        }

        matchRepository.saveAll(matches);
    }

    public void updateResults(UUID tournamentId) {
        List<MatchEntity> matches = matchRepository.findFinishedByTournamentId(tournamentId);
        //<gamer's UUID, points>
        Map<String, Double> pointsMap = new HashMap<>();
        Map<String, Set<String>> enemiesMap = new HashMap<>();
        Map<String, Double> buchholzMap = new HashMap<>();
        for (MatchEntity match : matches) {
            String white = match.getParticipant1() != null ? match.getParticipant1().getId().toString() : null;
            String black = match.getParticipant2() != null ? match.getParticipant2().getId().toString() : null;
            @Nullable MatchResult result = match.getResult();
            if (result == null) {
                addResult(pointsMap, enemiesMap, white, black, 0);
                addResult(pointsMap, enemiesMap, black, white, 0);
            } else switch (result) {
                case WHITE_WIN -> {
                    addResult(pointsMap, enemiesMap, white, black, 1);
                    addResult(pointsMap, enemiesMap, black, white, 0);
                }
                case BLACK_WIN -> {
                    addResult(pointsMap, enemiesMap, black, white, 1);
                    addResult(pointsMap, enemiesMap, white, black, 0);
                }
                case DRAW -> {
                    addResult(pointsMap, enemiesMap, white, black, 0.5);
                    addResult(pointsMap, enemiesMap, black, white, 0.5);
                }
                case BUY -> {
                    addResult(pointsMap, enemiesMap, white, black, 1);
                    addResult(pointsMap, enemiesMap, black, white, 1);
                }
                case MISS -> {
                    // Is miss, then no action is required because players did not play
                }
            }
        }
        enemiesMap.forEach((player, enemiesSet) -> {
            buchholzMap.putIfAbsent(player, 0d);
            for (String enemy : enemiesSet) {
                buchholzMap.computeIfPresent(player, (__, b) -> b + pointsMap.get(enemy));
            }
        });

        List<ParticipantEntity> participants = participantRepository.findByTournamentId(tournamentId);
        for (ParticipantEntity participant : participants) {
            final var pId = participant.getId().toString();
            participant.setScore(BigDecimal.valueOf(pointsMap.getOrDefault(pId, 0d)));
            participant.setBuchholz(BigDecimal.valueOf(buchholzMap.getOrDefault(pId, 0d)));
        }

        List<RoundEntity> tournamentRoundEntities = roundRepository.findByTournamentId(tournamentId);
        participants.sort(Comparator.comparing(ParticipantEntity::getScore)
                        .thenComparing((participant1, participant2) -> {
                            ParticipantEntity winnerBetweenTwoParticipants = findWinnerBetweenTwoParticipants(participant1, participant2, tournamentRoundEntities);
                            if (winnerBetweenTwoParticipants != null && winnerBetweenTwoParticipants.equals(participant1)) {
                                return 1;
                            } else if (winnerBetweenTwoParticipants != null && winnerBetweenTwoParticipants.equals(participant2)) {
                                return -1;
                            } else {
                                return 0;
                            }
                        })
                        .thenComparing(ParticipantEntity::getBuchholz)
                        .thenComparing(Comparator.comparing(ParticipantEntity::isMissing).reversed())
                        .thenComparing(ParticipantEntity::getNickname)
        );
        final int size = participants.size();
        for (int i = 0; i < size; ++i) {
            final var participant = participants.get(i);
            participant.setPlace(size - i);
        }
        participantRepository.saveAll(participants);
    }

    private static ParticipantEntity findWinnerBetweenTwoParticipants(ParticipantEntity first, ParticipantEntity second, List<RoundEntity> roundsDto) {
        for (RoundEntity round : roundsDto) {
            if (round.getMatches() != null) {
                for (MatchEntity match : round.getMatches()) {
                    if (match.getParticipant1() != null && match.getParticipant2() != null) {
                        if (match.getParticipant1().equals(first) && match.getParticipant2().equals(second)) {
                            if (match.getResult() == MatchResult.WHITE_WIN) {
                                return match.getParticipant1();
                            } else if (match.getResult() == MatchResult.BLACK_WIN) {
                                return match.getParticipant2();
                            }
                        } else if (match.getParticipant1().equals(second) && match.getParticipant2().equals(first)) {
                            if (match.getResult() == MatchResult.WHITE_WIN) {
                                return match.getParticipant1();
                            } else if (match.getResult() == MatchResult.BLACK_WIN) {
                                return match.getParticipant2();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void addResult(Map<String, Double> points, Map<String, Set<String>> enemies, @Nullable String player, @Nullable String enemy, double pointsToAdd) {
        if (player != null) {
            points.putIfAbsent(player, 0d);
            points.computeIfPresent(player, (__, p) -> p + pointsToAdd);
            enemies.putIfAbsent(player, new HashSet<>());
            if (enemy != null) {
                enemies.get(player).add(enemy);
            }
        }
    }

    public void markUserAsMissedInTournament(UUID userId, UUID tournamentId) {
        ParticipantEntity participantEntity = participantRepository.findByTournamentIdAndUserId(tournamentId, userId);
        participantEntity.setMissing(true);
        participantRepository.save(participantEntity);
    }

    public void deleteRound(UUID tournamentId, Integer roundNumber) throws RoundNotFoundException {
        RoundEntity roundEntity = roundRepository.findByTournamentIdAndNumber(tournamentId, roundNumber);

        if (roundEntity == null) {
            log.error("There is no round with number: " + roundNumber + " in the tournament with id: " + tournamentId);
            throw new RoundNotFoundException();
        }
        roundRepository.delete(roundEntity);
        List<RoundEntity> allRoundsWithGreaterRoundNumber = roundRepository.findAllRoundsWithGreaterRoundNumber(tournamentId, roundNumber);
        allRoundsWithGreaterRoundNumber.forEach(round -> round.setNumber(round.getNumber() - 1));
        roundRepository.saveAll(allRoundsWithGreaterRoundNumber);
        try {
            updateResults(tournamentId);
        } catch (Exception e) {
            log.error("Could not update results", e);
        }
    }
}
