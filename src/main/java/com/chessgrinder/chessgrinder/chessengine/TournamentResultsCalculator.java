package com.chessgrinder.chessgrinder.chessengine;

import com.chessgrinder.chessgrinder.dto.MatchDto;
import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class TournamentResultsCalculator {
    private final List<ParticipantDto> participants;
    private final List<MatchDto> matchHistory;

    private boolean isCalculated = false;

    private final Map<String, Double> pointsMap = new HashMap<>();
    private final Map<String, Double> buchholzMap = new HashMap<>();
    private final Map<String, Set<String>> opponentsMap = new HashMap<>();

    public TournamentResultsCalculator(List<ParticipantDto> participants, List<MatchDto> matchHistory) {
        this.participants = participants;
        this.matchHistory = matchHistory;
    }

    public void calculate() {
        for (MatchDto match : matchHistory) {
            String white = match.getWhite() != null ? match.getWhite().getId() : null;
            String black = match.getBlack() != null ? match.getBlack().getId() : null;
            @Nullable MatchResult result = match.getResult();
            if (result == null) {
                addResult(pointsMap, opponentsMap, white, black, 0);
                addResult(pointsMap, opponentsMap, black, white, 0);
            } else switch (result) {
                case WHITE_WIN -> {
                    addResult(pointsMap, opponentsMap, white, black, 1);
                    addResult(pointsMap, opponentsMap, black, white, 0);
                }
                case BLACK_WIN -> {
                    addResult(pointsMap, opponentsMap, black, white, 1);
                    addResult(pointsMap, opponentsMap, white, black, 0);
                }
                case DRAW -> {
                    addResult(pointsMap, opponentsMap, white, black, 0.5);
                    addResult(pointsMap, opponentsMap, black, white, 0.5);
                }
                case BUY -> {
                    addResult(pointsMap, opponentsMap, white, black, 1);
                    addResult(pointsMap, opponentsMap, black, white, 1);
                }
            }
        }
        opponentsMap.forEach((player, enemiesSet) -> {
            buchholzMap.putIfAbsent(player, 0d);
            for (String enemy : enemiesSet) {
                buchholzMap.put(player, buchholzMap.getOrDefault(player, 0d) + pointsMap.get(enemy));
            }
        });
        isCalculated = true;
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

    public List<ParticipantDto> getResult() {
        if (!isCalculated) calculate();
        return participants.stream()
                .map(it -> {
                            it.setScore(BigDecimal.valueOf(pointsMap.getOrDefault(it.getId(), 0d)));
                            it.setBuchholz(BigDecimal.valueOf(buchholzMap.getOrDefault(it.getId(), 0d)));
                            return it;
                        }
                )
                .collect(Collectors.toList());
    }
}
