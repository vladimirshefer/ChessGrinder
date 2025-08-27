package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.chessengine.pairings.JavafoPairingStrategyImpl;
import com.chessgrinder.chessgrinder.chessengine.pairings.PairingStrategy;
import com.chessgrinder.chessgrinder.chessengine.pairings.RoundRobinPairingStrategyImpl;
import com.chessgrinder.chessgrinder.dto.MatchDto;
import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoundEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.exceptions.RoundNotFoundException;
import com.chessgrinder.chessgrinder.mappers.MatchMapper;
import com.chessgrinder.chessgrinder.mappers.ParticipantMapper;
import com.chessgrinder.chessgrinder.repositories.MatchRepository;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.RoundRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.util.Graph;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

import static com.chessgrinder.chessgrinder.comparator.ParticipantEntityComparators.COMPARE_PARTICIPANT_ENTITY_BY_BUCHHOLZ_NULLSLAST;
import static com.chessgrinder.chessgrinder.comparator.ParticipantEntityComparators.COMPARE_PARTICIPANT_ENTITY_BY_SCORE_NULLS_LAST;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoundService {
    private final TournamentRepository tournamentRepository;
    private final RoundRepository roundRepository;
    private final ParticipantRepository participantRepository;
    private final JavafoPairingStrategyImpl javafoPairingStrategy;
    private final RoundRobinPairingStrategyImpl roundRobinPairingStrategy;

    private PairingStrategy getPairingStrategy(String name) {
        if ("SWISS".equals(name)) return javafoPairingStrategy;
        if ("ROUND_ROBIN".equals(name)) return roundRobinPairingStrategy;
        return javafoPairingStrategy;
    }

    private final MatchRepository matchRepository;

    private final MatchMapper matchMapper;

    private final ParticipantMapper participantMapper;

    @Transactional
    public void createRound(UUID tournamentId) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentId).orElseThrow();
        if (!tournamentEntity.getStatus().equals(TournamentStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not create round in non-active tournament");
        }

        // Try finish all other rounds
        List<RoundEntity> roundsInTournament = roundRepository.findByTournamentId(tournamentId);
        for (RoundEntity roundEntity : roundsInTournament) {
            if (!roundEntity.isFinished()) {
                finishRound(tournamentId, roundEntity.getNumber());
            }
        }

        RoundEntity lastExistedRoundEntity = roundRepository.findFirstByTournamentIdOrderByNumberDesc(tournamentId);
        Integer nextRoundNumber = lastExistedRoundEntity != null ? lastExistedRoundEntity.getNumber() + 1 : 1;
        if (nextRoundNumber > tournamentEntity.getRoundsNumber()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't add new round. Change rounds number in settings");
        }

        RoundEntity nextRoundEntity = RoundEntity.builder()
                .id(UUID.randomUUID())
                .number(nextRoundNumber)
                .tournament(tournamentEntity)
                .matches(List.of())
                .isFinished(false)
                .build();

        roundRepository.save(nextRoundEntity);
    }

    /**
     * @throws RuntimeException If any match has unknown result.
     */
    @Transactional
    public void finishRound(UUID tournamentId, Integer roundNumber) {
        RoundEntity roundEntity = roundRepository.findByTournamentIdAndNumber(tournamentId, roundNumber);
        if (roundEntity == null || roundEntity.isFinished()) {
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

    public boolean isReadyToFinish(RoundEntity roundEntity) {
        if (roundEntity.isFinished()) {
            return true;
        }
        for (MatchEntity match : roundEntity.getMatches()) {
            if (match.getResult() == null) {
                return false;
            }
        }
        return true;
    }

    public void reopenRound(UUID tournamentId, Integer roundNumber) {
        RoundEntity roundEntity = roundRepository.findByTournamentIdAndNumber(tournamentId, roundNumber);
        if (roundEntity == null) {
            return;
        }
        if (!roundEntity.getTournament().getStatus().equals(TournamentStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tournament is not active. Round could not be reopened");
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

        Optional<TournamentEntity> currentTournament = tournamentRepository.findById(tournamentId);
        TournamentEntity tournamentEntity = currentTournament.orElseThrow();
        if (tournamentEntity.getStatus().equals(TournamentStatus.PLANNED)) {
            tournamentEntity.setStatus(TournamentStatus.ACTIVE);
            tournamentEntity = tournamentRepository.save(tournamentEntity);
        }

        var allRounds = roundRepository.findByTournamentId(tournamentId);
        for (RoundEntity r : allRounds) {
            if (!r.isFinished()) {
                if (!r.getId().equals(round.getId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Finish all rounds before pairing a new one");
                }
            }
        }

        List<MatchEntity> alreadyExistedMatches = matchRepository.findMatchEntitiesByRoundId(round.getId());
        if (alreadyExistedMatches.stream().anyMatch(it -> it.getResult() != null && it.getResult() != MatchResult.BUY && it.getResult() != MatchResult.MISS)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not make pairing because results already submitted. Try remove all results manually.");
        } else {
            matchRepository.deleteAll(alreadyExistedMatches);
        }

        List<ParticipantEntity> participantEntities = participantRepository.findByTournamentId(tournamentId);
        List<ParticipantDto> participantDtos = participantMapper.toDto(participantEntities);

        TournamentEntity tournament = tournamentRepository.findById(tournamentId).orElseThrow();

        List<List<MatchEntity>> allMatchesInTheTournament = tournament.getRounds().stream()
                .filter(RoundEntity::isFinished)
                .map(RoundEntity::getMatches)
                .toList();

        List<List<MatchDto>> allMatches = allMatchesInTheTournament.stream().map(matchMapper::toDto).toList();

        String pairingStrategy = tournament.getPairingStrategy();
        PairingStrategy strategy = getPairingStrategy(pairingStrategy);
        List<MatchDto> matchesDto = strategy.makePairings(participantDtos, allMatches, tournament.getRoundsNumber());
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

        List<ParticipantEntity> participants = new ArrayList<>(participantRepository.findByTournamentId(tournamentId));
        for (ParticipantEntity participant : participants) {
            final var pId = participant.getId().toString();
            participant.setScore(BigDecimal.valueOf(pointsMap.getOrDefault(pId, 0d)));
            participant.setBuchholz(BigDecimal.valueOf(buchholzMap.getOrDefault(pId, 0d)));
        }

        List<RoundEntity> tournamentRoundEntities = roundRepository.findByTournamentId(tournamentId);
        Graph<ParticipantEntity> graph = buildEncounterGraph(tournamentRoundEntities);

        participants.sort(COMPARE_PARTICIPANT_ENTITY_BY_SCORE_NULLS_LAST
                .thenComparing(compareParticipantEntityByPersonalEncounterWinnerFirst(graph))
                .thenComparing(COMPARE_PARTICIPANT_ENTITY_BY_BUCHHOLZ_NULLSLAST)
                .thenComparing(ParticipantEntity::isMissing)
                .thenComparing(ParticipantEntity::getNickname, nullsLast(naturalOrder()))
        );
        final int size = participants.size();
        for (int i = 0; i < size; ++i) {
            final var participant = participants.get(i);
            participant.setPlace(i + 1);
        }
        participantRepository.saveAll(participants);
    }

    @VisibleForTesting
    static <T> Comparator<T> compareParticipantEntityByPersonalEncounterWinnerFirst(Graph<T> directEncounters) {
        return (e1, e2) -> {
            boolean transitivelyWon12 = directEncounters.dfs(e1).anyMatch(e2::equals);
            boolean transitivelyWon21 = directEncounters.dfs(e2).anyMatch(e1::equals);
            if (transitivelyWon12 && transitivelyWon21) {
                return 0;
            }
            if (transitivelyWon12) {
                return -1;
            }
            if (transitivelyWon21) {
                return 1;
            }
            return 0;
        };
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
        if (roundEntity.getTournament().getStatus() == TournamentStatus.FINISHED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not delete round in finished tournament.");
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

    @VisibleForTesting
    static Graph<ParticipantEntity> buildEncounterGraph(List<RoundEntity> rounds) {
        return new Graph<>(rounds
                .stream()
                .flatMap(round -> round.getMatches().stream())
                .filter(match -> match.getResult() != null)
                .filter(match -> match.getParticipant1() != null && match.getParticipant2() != null)
                .filter(match -> Objects.equals(match.getParticipant1().getScore(), match.getParticipant2().getScore()))
                .map(RoundService::toWinnerAndLoser)
                .filter(Objects::nonNull).toList()
        );
    }

    @Nullable
    private static SimpleEntry<ParticipantEntity, ParticipantEntity> toWinnerAndLoser(MatchEntity match) {
        ParticipantEntity p1 = match.getParticipant1();
        ParticipantEntity p2 = match.getParticipant2();
        if (p1 == null || p2 == null) {
            return null;
        }

        if (match.getResult() == MatchResult.WHITE_WIN) {
            return new SimpleEntry<>(p1, p2);
        } else if (match.getResult() == MatchResult.BLACK_WIN) {
            return new SimpleEntry<>(p2, p1);
        }
        return null;
    }

}
