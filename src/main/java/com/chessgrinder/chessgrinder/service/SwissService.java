package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.dto.internals.*;
import com.chessgrinder.chessgrinder.enums.*;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.*;

@Component
@RequiredArgsConstructor
@Getter
public class SwissService {

    /**
     * "Main" method for swiss service. Takes list of all participants and return list of all matches.
     * @param participants
     * @return
     */
    public List<MatchDto> makePairs(List<ParticipantDto> participants) {

        List<MatchDto> matches = new ArrayList<>();

        if (participants.isEmpty()) return Collections.emptyList();

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

            ParticipantForPairing secondCandidateForMatch = findPairForParticipant(listOfAllPlayers, firstCandidateForMatch);

            MatchDto match = createMatchBetweenTwoParticipants(firstCandidateForMatch, secondCandidateForMatch);
            matches.add(match);
        }

        return matches;
    }

    /**
     *  Returns secondCandidateForMatch. Finds the closest score gap and selects first player from the second half of group.
     * @param players all participants in the tournament
     * @param firstCandidateForMatch first candidate for match.
     * @return
     */
    public ParticipantForPairing findPairForParticipant(List<ParticipantForPairing> players, ParticipantForPairing firstCandidateForMatch) {

        List<ScoreModel> separateParticipantsByScores = sortParticipantsByScores(players);
        List<ScoreModel> scoreModels = makeEvenValues(separateParticipantsByScores);

        ScoreModel scoreModelWithClosestScore = findScoreModelWithClosestScore(scoreModels, firstCandidateForMatch);

        List<ParticipantForPairing> participants = scoreModelWithClosestScore.getParticipants();
        var split = split(participants);

        if (split.length == 0) {
            return null;
        }

        return (ParticipantForPairing) split[1].get(0);
    }


    /**
     *  SecondCandidateForMatch should have the closest score to the firstCandidateForMatch. This method returns ScoreModel
     *  which represents list of possible candidates.
     * @param scoreModels
     * @param firstCandidateForMatch
     * @return
     */
    public ScoreModel findScoreModelWithClosestScore(List<ScoreModel> scoreModels, ParticipantForPairing firstCandidateForMatch) {
        if (scoreModels.isEmpty()) {
            return null;
        }

        BigDecimal targetScore = firstCandidateForMatch.getParticipant().getScore();
        ScoreModel closestScoreModel = scoreModels.get(0); // Initialize with the first ScoreModel

        BigDecimal closestDifference = targetScore.subtract(closestScoreModel.getScore()).abs();

        for (ScoreModel scoreModel : scoreModels) {
            BigDecimal difference = targetScore.subtract(scoreModel.getScore()).abs();

            if (difference.compareTo(closestDifference) < 0) {
                // If the current ScoreModel has a closer score
                closestDifference = difference;
                closestScoreModel = scoreModel;
            }
        }

        return closestScoreModel;
    }


    public static<T> List[] split(List<T> list) {
        if (list.isEmpty() || list.size() == 1) {
            return new List[]{};
        }
        List<List<T>> lists = Lists.partition(list, (list.size() + 1) / 2);
        return new List[] {lists.get(0), lists.get(1)};
    }


    public List<ScoreModel> makeEvenValues(List<ScoreModel> scoreModels) {

        int lastIndex = scoreModels.size() - 1;

        for (int i = 0; i < scoreModels.size() - 1; i++) {
            ScoreModel currentModel = scoreModels.get(i);
            ScoreModel nextModel = scoreModels.get(i + 1);

            if (currentModel.getParticipants().size() % 2 != 0) {
                ParticipantForPairing participantToMove = nextModel.getParticipants().get(0);
                currentModel.getParticipants().add(participantToMove);
                nextModel.getParticipants().remove(0);
            }
        }

        ScoreModel lastModel = scoreModels.get(lastIndex);
        if (lastModel.getParticipants().size() % 2 != 0) {
            ScoreModel newModel = ScoreModel.builder()
                    .score(BigDecimal.valueOf(-1))
                    .participants(new ArrayList<>())
                    .build();
            newModel.getParticipants().add(lastModel.getParticipants().get(lastModel.getParticipants().size() - 1));
            lastModel.getParticipants().remove(lastModel.getParticipants().size() - 1);
            scoreModels.add(newModel);
        }

        scoreModels.removeIf(model -> model.getParticipants().isEmpty());

        return scoreModels;
    }

    /**
     *  Creates MatchDto object for two participants in the tournament.
     *  The player who has played fewer games with the white pieces in the tournament will now be playing as white.
     *
     * @param first player
     * @param second player
     * @return MatchDto
     */
    public MatchDto createMatchBetweenTwoParticipants(ParticipantForPairing first, ParticipantForPairing second) {

        MatchDto match;

        if (second == null) {

            return MatchDto.builder()
                    .white(first.getParticipant())
                    .result(MatchResult.BUY)
                    .build();
        }

        if (first.getTimesPlayedWhiteInTournament() <= second.getTimesPlayedWhiteInTournament()) {
            match = MatchDto.builder()
                    .white(first.getParticipant())
                    .black(second.getParticipant())
                    .build();
        } else {
            match = MatchDto.builder()
                    .white(second.getParticipant())
                    .black(first.getParticipant())
                    .build();
        }

        first.setBooked(true);
        second.setBooked(true);

        return match;
    }

    /**
     *  Returns list of special ScoreModel objects which groups and sorts all participants by scores.
     *
     * @param participants All players in the tournament.
     * @return List<ScoreModel>
     */
    public static List<ScoreModel> sortParticipantsByScores(List<ParticipantForPairing> participants) {

        // Filter participants who have not booked.
        List<ParticipantForPairing> notBookedParticipants = participants.stream()
                .filter(player -> !player.isBooked()).toList();

        // Group participants by their scores.
        Map<BigDecimal, List<ParticipantForPairing>> groupedParticipants = notBookedParticipants.stream()
                .collect(Collectors.groupingBy(
                        participant -> participant.getParticipant().getScore(),
                        Collectors.toList()
                ));

        // Convert the grouped participants into ScoreModel objects.
        List<ScoreModel> scoreModels = new ArrayList<>();
        for (Map.Entry<BigDecimal, List<ParticipantForPairing>> entry : groupedParticipants.entrySet()) {
            ScoreModel scoreModel = ScoreModel.builder()
                    .score(entry.getKey())
                    .participants(entry.getValue())
                    .build();
            scoreModels.add(scoreModel);
        }

        // Sorting by score.
        scoreModels.sort((a, b) -> b.getScore().compareTo(a.getScore()));

        return scoreModels;
    }

}


