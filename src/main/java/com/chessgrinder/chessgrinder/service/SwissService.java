package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.dto.internals.*;
import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.repositories.MatchRepository;
import com.google.common.collect.Lists;
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
    private String tournamentId;

    public List<MatchDto> makePairs(List<ParticipantDto> participants) {

        List<MatchEntity> matches = new ArrayList<>();

        if (participants.isEmpty()) return Collections.emptyList();

/*
        tournamentId = participants.get(0).getTournament().getId();
*/

        List<ParticipantDto> sortedParticipants = participants.stream()
                .sorted(Comparator.comparing(ParticipantDto::getScore).reversed()).toList();

        List<ParticipantForPairing> listOfAllPlayers = new ArrayList<>(

                sortedParticipants.stream().map(participant -> {
                    ParticipantForPairing participantForPairing = new ParticipantForPairing();
                    participantForPairing.setParticipant(participant);
                    return participantForPairing;
                }).toList()

        );

        for (ParticipantForPairing firstCandidateForMatch : listOfAllPlayers) {

            if (firstCandidateForMatch.isBooked()) {
                continue;
            }

            firstCandidateForMatch.setBooked(true);
            ParticipantDto secondCandidateForMatch = findMatchForParticipant(listOfAllPlayers, firstCandidateForMatch);

        }

        return null;
    }
    public ParticipantDto findMatchForParticipant(List<ParticipantForPairing> players, ParticipantForPairing firstCandidateForMatch) {
        List<ScoreModel> separateParticipantsByScores = separateParticipantsByScores(players);
        List<ScoreModel> scoreModels = makeEvenValues(separateParticipantsByScores);

        return null;
    }


    public static<T> List[] split(List<T> list) {
        if (list.isEmpty()) {
            return new List[]{};
        }
        List<List<T>> lists = Lists.partition(list, (list.size() + 1) / 2);
        return new List[] {lists.get(0), lists.get(1)};
    }

    public List<ScoreModel> separateParticipantsByScores (List<ParticipantForPairing> participants) {

        Map<BigDecimal, List<ParticipantForPairing>> map = new TreeMap<>(Collections.reverseOrder());
        List<ScoreModel> resultList = new ArrayList<>();

        for (ParticipantForPairing participant: participants) {
            BigDecimal score = participant.getParticipant().getScore();

            List<ParticipantForPairing> participantsWithSameScore = map.getOrDefault(score, new ArrayList<>());

            participantsWithSameScore.add(participant);

            ScoreModel scoreModel = ScoreModel.builder().score(score).participants(participantsWithSameScore).build();
            resultList.add(scoreModel);

        }

        return resultList;
    }

    public List<ScoreModel> makeEvenValues(List<ScoreModel> scoreModels) {

        int currentIndex = 0;
        List<ScoreModel> modelsToRemove = new ArrayList<>();

        while (currentIndex < scoreModels.size()) {
            ScoreModel currentScoreModel = scoreModels.get(currentIndex);

            if (currentScoreModel.getParticipants().size() % 2 != 0) {
                // Current ScoreModel has an odd number of participants
                if (currentIndex < scoreModels.size() - 1) {
                    // There is a next ScoreModel available
                    ScoreModel nextScoreModel = scoreModels.get(currentIndex + 1);
                    ParticipantForPairing firstParticipant = nextScoreModel.getParticipants().get(0);
                    currentScoreModel.getParticipants().add(firstParticipant);
                    nextScoreModel.getParticipants().remove(0);
                } else {
                    // There is no next ScoreModel, so we cannot fix it
                    break;
                }
            } else {
                // Current ScoreModel has an even number of participants, no action needed
                currentIndex++;
            }

            if (currentScoreModel.getParticipants().isEmpty()) {
                // Mark the current ScoreModel for removal
                modelsToRemove.add(currentScoreModel);
            }
        }
        scoreModels.removeAll(modelsToRemove);
        return scoreModels;
    }



}


