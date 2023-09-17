package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.Match;
import com.chessgrinder.chessgrinder.entities.Participant;
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

    public List<Match> makePairs(List<Participant> participants) {
        if (participants.isEmpty()) return Collections.emptyList();

        List<Match> matches = new ArrayList<>();

        tournamentId = participants.get(0).getTournament().getId();

        List<Participant> sortedParticipants = participants.stream()
                .sorted(Comparator.comparing(Participant::getScore).reversed()).toList();

        List<ParticipantForPairing> participantForPairingList = new ArrayList<>(sortedParticipants.stream()
                .map(participant -> {
                    ParticipantForPairing participantForPairing = new ParticipantForPairing();
                    participantForPairing.setParticipant(participant);
                    return participantForPairing;
                }).toList());

        for (ParticipantForPairing participant1 : participantForPairingList) {

            if (participant1.isBooked) {
                continue;
            }

            Match match = Match.builder()
                    .id(UUID.randomUUID())
                    .participant1(participant1.getParticipant())
                    .build();

            participant1.setBooked(true);
            Match matchForParticipant = findMatchForParticipant(participantForPairingList, match);
            matches.add(matchForParticipant);
        }

        return matches;
    }

    public Map<BigDecimal, List<Participant>> separateParticipantsByScores (List<Participant> participants) {
        Map<BigDecimal, List<Participant>> map = new TreeMap<>(Collections.reverseOrder());
        for (Participant participant : participants) {
            BigDecimal score = participant.getScore();

            List<Participant> participantsWithSameScore = map.getOrDefault(score, new ArrayList<>());

            participantsWithSameScore.add(participant);
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

    public Match findMatchForParticipant(List<ParticipantForPairing> players, Match match) {

        Participant first = match.getParticipant1();
        for (ParticipantForPairing participantForPairing: players) {

            Participant second = participantForPairing.getParticipant();
            Match hasMatchWithTwoPlayersBeenInTournament = matchRepository.findMatchBetweenTwoParticipantsInTournament(tournamentId, first, second);

            if (hasMatchWithTwoPlayersBeenInTournament == null && !participantForPairing.isBooked()) {
                match.setParticipant2(second);
                participantForPairing.setBooked(true);
                return match;
            }
        }

        return match;
    }
}

@Data
class ParticipantForPairing {
    Participant participant;
    boolean isBooked = false;
}

