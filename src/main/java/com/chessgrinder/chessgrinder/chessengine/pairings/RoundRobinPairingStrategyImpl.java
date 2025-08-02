package com.chessgrinder.chessgrinder.chessengine.pairings;

import com.chessgrinder.chessgrinder.dto.MatchDto;
import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import org.springframework.stereotype.Component;
import java.util.*;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

/**
 * RoundRobinPairingStrategyImpl is a class for creating pairings based on Berger tables.
 * In this format, each player competes against every other player exactly once.
 * This class is applicable when the number of players is between 4 and 16, inclusive.
 */

@Component
public class RoundRobinPairingStrategyImpl implements PairingStrategy {


    //Berger tables completed according to FIDE rules
    private static final int[][] bergerTable4 = {
            {0, 3, 1, 2}, // Round 1: Matches: (1,4/bye), (2,3)
            {3, 2, 0, 1}, // Round 2: Matches: (4/bye,3), (1,2)
            {1, 3, 2, 0}, // Round 3: Matches: (2,4/bye), (3,1)
    };

    private static final int[][] bergerTable6 = {
            {0, 5, 1, 4, 2, 3}, // Round 1: Matches: (1,6/bye), (2,5), (3,4)
            {5, 3, 4, 2, 0, 1}, // Round 2: Matches: (6/bye,4), (5,3), (1,2)
            {1, 5, 2, 0, 3, 4}, // Round 3: Matches: (2,6/bye), (3,1), (4,5)
            {5, 4, 0, 3, 1, 2}, // Round 4: Matches: (6/bye,5), (1,4), (2,3)
            {2, 5, 3, 1, 4, 0}  // Round 5: Matches: (3,6/bye), (4,2), (5,1)
    };

    private static final int[][] bergerTable8 = {
            {0, 7, 1, 6, 2, 5, 3, 4},    // Round 1
            {7, 4, 5, 3, 6, 2, 0, 1},    // Round 2
            {1, 7, 2, 0, 3, 6, 4, 5},    // Round 3
            {7, 5, 6, 4, 0, 3, 1, 2},    // Round 4
            {2, 7, 3, 1, 4, 0, 5, 6},    // Round 5
            {7, 6, 0, 5, 1, 4, 2, 3},    // Round 6
            {3, 7, 4, 2, 5, 1, 6, 0}     // Round 7
    };

    private static final int[][] bergerTable10 = {
            {0, 9, 1, 8, 2, 7, 3, 6, 4, 5},    // Round 1
            {9, 5, 6, 4, 7, 3, 8, 2, 0, 1},    // Round 2
            {1, 9, 2, 0, 3, 8, 4, 7, 5, 6},    // Round 3
            {9, 6, 7, 5, 8, 4, 0, 3, 1, 2},    // Round 4
            {2, 9, 3, 1, 4, 0, 5, 8, 6, 7},    // Round 5
            {9, 7, 8, 6, 0, 5, 1, 4, 2, 3},    // Round 6
            {3, 9, 4, 2, 5, 1, 6, 0, 7, 8},    // Round 7
            {9, 8, 0, 7, 1, 6, 2, 5, 3, 4},    // Round 8
            {4, 9, 5, 3, 6, 2, 7, 1, 8, 0}     // Round 9
    };

    private static final int[][] bergerTable12 = {
            {0, 11, 1, 10, 2, 9, 3, 8, 4, 7, 5, 6},    // Round 1
            {11, 6, 7, 5, 8, 4, 9, 3, 10, 2, 0, 1},    // Round 2
            {1, 11, 2, 0, 3, 10, 4, 9, 5, 8, 6, 7},    // Round 3
            {11, 7, 8, 6, 9, 5, 10, 4, 0, 3, 1, 2},    // Round 4
            {2, 11, 3, 1, 4, 0, 5, 10, 6, 9, 7, 8},    // Round 5
            {11, 8, 9, 7, 10, 6, 0, 5, 1, 4, 2, 3},    // Round 6
            {3, 11, 4, 2, 5, 1, 6, 0, 7, 10, 8, 9},    // Round 7
            {11, 9, 10, 8, 0, 7, 1, 6, 2, 5, 3, 4},    // Round 8
            {4, 11, 5, 3, 6, 2, 7, 1, 8, 0, 9, 10},    // Round 9
            {11, 10, 0, 9, 1, 8, 2, 7, 3, 6, 4, 5},    // Round 10
            {5, 11, 6, 4, 7, 3, 8, 2, 9, 1, 10, 0}     // Round 11
    };

    private static final int[][] bergerTable14 = {
            {0, 13, 1, 12, 2, 11, 3, 10, 4, 9, 5, 8, 6, 7},     // Round 1
            {13, 7, 8, 6, 9, 5, 10, 4, 11, 3, 12, 2, 0, 1},     // Round 2
            {1, 13, 2, 0, 3, 12, 4, 11, 5, 10, 6, 9, 7, 8},     // Round 3
            {13, 8, 9, 7, 10, 6, 11, 5, 12, 4, 0, 3, 1, 2},     // Round 4
            {2, 13, 3, 1, 4, 0, 5, 12, 6, 11, 7, 10, 8, 9},     // Round 5
            {13, 9, 10, 8, 11, 7, 12, 6, 0, 5, 1, 4, 2, 3},     // Round 6
            {3, 13, 4, 2, 5, 1, 6, 0, 7, 12, 8, 11, 9, 10},     // Round 7
            {13, 10, 11, 9, 12, 8, 0, 7, 1, 6, 2, 5, 3, 4},     // Round 8
            {4, 13, 5, 3, 6, 2, 7, 1, 8, 0, 9, 12, 10, 11},     // Round 9
            {13, 11, 12, 10, 0, 9, 1, 8, 2, 7, 3, 6, 4, 5},     // Round 10
            {5, 13, 6, 4, 7, 3, 8, 2, 9, 1, 10, 0, 11, 12},     // Round 11
            {13, 12, 0, 11, 1, 10, 2, 9, 3, 8, 4, 7, 5, 6},     // Round 12
            {6, 13, 7, 5, 8, 4, 9, 3, 10, 2, 11, 1, 12, 0},     // Round 13
    };

    private static final int[][] bergerTable16 = {
            {0, 15, 1, 14, 2, 13, 3, 12, 4, 11, 5, 10, 6, 9, 7, 8},   // Round 1
            {15, 8, 9, 7, 10, 6, 11, 5, 12, 4, 13, 3, 14, 2, 0, 1},   // Round 2
            {1, 15, 2, 0, 3, 14, 4, 13, 5, 12, 6, 11, 7, 10, 8, 9},   // Round 3
            {15, 9, 10, 8, 11, 7, 12, 6, 13, 5, 14, 4, 0, 3, 1, 2},   // Round 4
            {2, 15, 3, 1, 4, 0, 5, 14, 6, 13, 7, 12, 8, 11, 9, 10},   // Round 5
            {15, 10, 11, 9, 12, 8, 13, 7, 14, 6, 0, 5, 1, 4, 2, 3},   // Round 6
            {3, 15, 4, 2, 5, 1, 6, 0, 7, 14, 8, 13, 9, 12, 10, 11},   // Round 7
            {15, 11, 12, 10, 13, 9, 14, 8, 0, 7, 1, 6, 2, 5, 3, 4},   // Round 8
            {4, 15, 5, 3, 6, 2, 7, 1, 8, 0, 9, 14, 10, 13, 11, 12},   // Round 9
            {15, 12, 13, 11, 14, 10, 0, 9, 1, 8, 2, 7, 3, 6, 4, 5},   // Round 10
            {5, 15, 6, 4, 7, 3, 8, 2, 9, 1, 10, 0, 11, 14, 12, 13},   // Round 11
            {15, 13, 14, 12, 0, 11, 1, 10, 2, 9, 3, 8, 4, 7, 5, 6},   // Round 12
            {6, 15, 7, 5, 8, 4, 9, 3, 10, 2, 11, 1, 12, 0, 13, 14},   // Round 13
            {15, 14, 0, 13, 1, 12, 2, 11, 3, 10, 4, 9, 5, 8, 6, 7},   // Round 14
            {7, 15, 8, 6, 9, 5, 10, 4, 11, 3, 12, 2, 13, 1, 14, 0}    // Round 15
    };


    // Following function counts the sum of players participated in the previous round.
    public int countPlayers(List<MatchDto> lastRound) {

        int playersCount = 0;

        if (lastRound.isEmpty()) {
            return 0;
        } else {

            for (MatchDto match : lastRound) {
                ParticipantDto white = match.getWhite();
                if (white != null && white.getIsMissing() != true) {
                    playersCount++;
                }
                ParticipantDto black = match.getBlack();
                if (black != null && black.getIsMissing() != true) {
                    playersCount++;
                }
            }
        }

        return playersCount;
    }

    @Override
    public List<MatchDto> makePairings(
            List<ParticipantDto> participants,
            List<List<MatchDto>> matchHistory,
            Integer roundsNumber,
            boolean recalculateResults
    ) {

        participants = new ArrayList<>(participants.stream().filter(it -> it != null && it.getIsMissing() != true).toList());

        participants.sort(Comparator.comparing(ParticipantDto::getId, nullsLast(naturalOrder())));

        int participantsNumber = participants.size();

        // We do not apply the round-robin format if the number of players is less than 3 or more than 16.
        if (participants.isEmpty() || participantsNumber < 3 || participantsNumber > 16) {
            throw new IllegalArgumentException("A pairing is not allowed if there are less than 3 or more than 16 players.");
        }

        // Check for additional players
        List<MatchDto> lastRound = matchHistory.isEmpty() ? new ArrayList<>() : matchHistory.get(matchHistory.size() - 1);
        int currentCounts = countPlayers(lastRound);
        if (currentCounts != participants.size() && currentCounts != 0) {
            throw new IllegalArgumentException("It is prohibited to add additional players");
        }

        // A player for a bye is adding in case of an odd number of players.
        if (participantsNumber % 2 != 0) {
            participants.add(null);
            participantsNumber = participantsNumber + 1;
        }

        Map<Integer, int[][]> getBergerTable = new HashMap<>();

        getBergerTable.put(3, bergerTable4); // For 3 players
        getBergerTable.put(4, bergerTable4); // For 4 players
        getBergerTable.put(5, bergerTable6); // For 5 players
        getBergerTable.put(6, bergerTable6); // For 6 players
        getBergerTable.put(7, bergerTable8); // For 7 players
        getBergerTable.put(8, bergerTable8); // For 8 players
        getBergerTable.put(9, bergerTable10); // For 9 players
        getBergerTable.put(10, bergerTable10); // For 10 players
        getBergerTable.put(11, bergerTable12); // For 11 players
        getBergerTable.put(12, bergerTable12); // For 12 players
        getBergerTable.put(13, bergerTable14); // For 13 players
        getBergerTable.put(14, bergerTable14); // For 14 players
        getBergerTable.put(15, bergerTable16); // For 15 players
        getBergerTable.put(16, bergerTable16); // For 16 players

        List<MatchDto> pairings = new ArrayList<>();

        int[][] bergerTable = getBergerTable.get(participantsNumber);

        for (int i = 0; i < participantsNumber; i += 2) {

            ParticipantDto whitePlayer = participants.get(bergerTable[matchHistory.size() % (participantsNumber - 1)][i]);
            ParticipantDto blackPlayer = participants.get(bergerTable[matchHistory.size() % (participantsNumber - 1)][(i + 1)]);

            MatchDto match = MatchDto.builder()
                    .white(whitePlayer)
                    .black(blackPlayer)
                    .result(whitePlayer == null || blackPlayer == null ? MatchResult.BUY : null)
                    .build();
            pairings.add(match);

        }

        return pairings;
    }
}