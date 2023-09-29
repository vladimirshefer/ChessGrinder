package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.repositories.MatchRepository;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
@RequiredArgsConstructor
@Getter
public class SwissService {

    private final MatchRepository matchRepository;
    private UUID tournamentId;

    public List<MatchEntity> makePairs(List<ParticipantEntity> participantEntities) {
        if (participantEntities.isEmpty()) return Collections.emptyList();

        List<MatchEntity> matchEntities = new ArrayList<>();

        tournamentId = participantEntities.get(0).getTournamentEntity().getId();

        List<ParticipantEntity> sortedParticipantEntities = participantEntities.stream()
                .sorted(Comparator.comparing(ParticipantEntity::getScore).reversed()).toList();

        List<ParticipantForPairing> participantForPairingList = new ArrayList<>(sortedParticipantEntities.stream()
                .map(participant -> {
                    ParticipantForPairing participantForPairing = new ParticipantForPairing();
                    participantForPairing.setParticipantEntity(participant);
                    return participantForPairing;
                }).toList());

        for (ParticipantForPairing participant1 : participantForPairingList) {

            if (participant1.isBooked) {
                continue;
            }

            MatchEntity matchEntity = MatchEntity.builder()
                    .id(UUID.randomUUID())
                    .participantEntity1(participant1.getParticipantEntity())
                    .build();

            participant1.setBooked(true);
            MatchEntity matchEntityForParticipant = findMatchForParticipant(participantForPairingList, matchEntity);
            matchEntities.add(matchEntityForParticipant);
        }

        return matchEntities;
    }

    public Map<BigDecimal, List<ParticipantEntity>> separateParticipantsByScores (List<ParticipantEntity> participantEntities) {
        Map<BigDecimal, List<ParticipantEntity>> map = new TreeMap<>(Collections.reverseOrder());
        for (ParticipantEntity participantEntity : participantEntities) {
            BigDecimal score = participantEntity.getScore();

            List<ParticipantEntity> participantsWithSameScore = map.getOrDefault(score, new ArrayList<>());

            participantsWithSameScore.add(participantEntity);
            map.put(score, participantsWithSameScore);

        }
        return map;
    }

    public static<T> List[] split(List<T> list) {
        if (list.isEmpty()) {
            return new List[]{};
        }
        List<List<T>> lists = Lists.partition(list, (list.size() + 1) / 2);
        return new List[] {lists.get(0), lists.get(1)};
    }

    public MatchEntity findMatchForParticipant(List<ParticipantForPairing> players, MatchEntity matchEntity) {

        ParticipantEntity first = matchEntity.getParticipantEntity1();
        for (ParticipantForPairing participantForPairing: players) {

            ParticipantEntity second = participantForPairing.getParticipantEntity();
            MatchEntity hasMatchWithTwoPlayersBeenInTournamentEntity = matchRepository.findMatchBetweenTwoParticipantsInTournament(tournamentId, first, second);

            if (hasMatchWithTwoPlayersBeenInTournamentEntity == null && !participantForPairing.isBooked()) {
                matchEntity.setParticipantEntity2(second);
                participantForPairing.setBooked(true);
                return matchEntity;
            }
        }

        return matchEntity;
    }
}

@Data
class ParticipantForPairing {
    ParticipantEntity participantEntity;
    boolean isBooked = false;
}

