package com.chessgrinder.chessgrinder.chessengine;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.dto.internals.*;
import com.chessgrinder.chessgrinder.enums.*;
import com.google.common.collect.Lists;
import lombok.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.*;

@Component
@RequiredArgsConstructor
@Getter
@Data
public class SwissMatchupStrategyImpl implements MatchupStrategy {

    /**
     * "Main" method for swiss service. Takes list of all participants and return list of all matches.
     *
     * @param participants
     * @param allMatchesInTheTournament
     * @return
     */
    public List<MatchDto> matchUp(List<ParticipantDto> participants, List<MatchDto> allMatchesInTheTournament) {

        List<MatchDto> matches = new ArrayList<>();

        if (participants.isEmpty()) {
            return Collections.emptyList();
        }

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

            ParticipantForPairing secondCandidateForMatch = findPairForParticipant(listOfAllPlayers, firstCandidateForMatch, allMatchesInTheTournament);

            MatchDto match = createMatchBetweenTwoParticipants(firstCandidateForMatch, secondCandidateForMatch);
            matches.add(match);
        }

        return matches;
    }

    /**
     * Returns secondCandidateForMatch. Finds the closest score gap and selects first player from the second half of group.
     *
     * @param players                   all participants in the tournament
     * @param firstCandidateForMatch    first candidate for match.
     * @param allMatchesInTheTournament
     * @return
     */
    public ParticipantForPairing findPairForParticipant(
            List<ParticipantForPairing> players,
            ParticipantForPairing firstCandidateForMatch,
            List<MatchDto> allMatchesInTheTournament
    ) {

        Set<String> userIdsToExclude = allMatchesInTheTournament.stream()
                .filter(matchDto -> matchDto.getWhite().getId().equals(firstCandidateForMatch.getParticipant().getId())
                        || matchDto.getBlack().getId().equals(firstCandidateForMatch.getParticipant().getId()))
                .map(matchDto -> {
                    if (matchDto.getWhite().getId().equals(firstCandidateForMatch.getParticipant().getId())) {
                        return matchDto.getBlack().getId();
                    } else {
                        return matchDto.getWhite().getId();
                    }
                })
                .collect(Collectors.toSet());

        userIdsToExclude.add(firstCandidateForMatch.getParticipant().getId());

        List<ParticipantForPairing> filteredPlayers = players.stream()
                .filter(player -> !userIdsToExclude.contains(player.getParticipant().getId()))
                .collect(Collectors.toList());

        List<ScoreModel> separateParticipantsByScores = sortParticipantsByScores(filteredPlayers, allMatchesInTheTournament);



        ScoreModel scoreModelWithClosestScore = findScoreModelWithClosestScore(separateParticipantsByScores, firstCandidateForMatch);

        List<ParticipantForPairing> participants = scoreModelWithClosestScore.getParticipants();
        var split = split(participants);

        if (split.length == 0) {
            return null;
        }

        if (split.length >= 2 && split[1] != null && !split[1].isEmpty()) {
            return (ParticipantForPairing) split[1].get(0);
        } else if (split[0] != null && !split[0].isEmpty()) {
            return (ParticipantForPairing) split[0].get(0);
        } else {
            return null;
        }

    }


    /**
     * SecondCandidateForMatch should have the closest score to the firstCandidateForMatch. This method returns ScoreModel
     * which represents list of possible candidates.
     *
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


    public static <T> List[] split(List<T> list) {
        if (list.isEmpty()) {
            return new List[]{};
        }

        if (list.size() == 1) {
            return new List[]{list};
        }

        List<List<T>> lists = Lists.partition(list, (list.size() + 1) / 2);
        return new List[]{lists.get(0), lists.get(1)};
    }


    /**
     * Creates MatchDto object for two participants in the tournament.
     * The player who has played fewer games with the white pieces in the tournament will now be playing as white.
     *
     * @param first  player
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
     * Returns list of special ScoreModel objects which groups and sorts all participants by scores.
     *
     * @param participants All players in the tournament.
     * @return List<ScoreModel>
     */
    public static List<ScoreModel> sortParticipantsByScores(
            List<ParticipantForPairing> participants,
            List<MatchDto> allMatchesInTheTournament) {

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


