package com.chessgrinder.chessgrinder.chessengine;

import com.chessgrinder.chessgrinder.dto.MatchDto;
import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class SwissCalculator {

    @Getter
    private final List<ParticipantDto> participants;
    @Getter
    private final List<MatchDto> matchHistory;

    private final Set<String> bookedParticipants = new HashSet<>();

    private final Map<String, Integer> timesPlayedWhite;

    public SwissCalculator(List<ParticipantDto> participants, List<MatchDto> matchHistory) {
        this.participants = participants.stream()
                .sorted(
                        Comparator.comparing(ParticipantDto::getScore).reversed()
                                .thenComparing(ParticipantDto::getBuchholz)
                                .thenComparing(ParticipantDto::getName)
                                .thenComparing(Comparator.nullsLast(Comparator.comparing(ParticipantDto::getUserId)))
                )
                .toList();
        this.matchHistory = Collections.unmodifiableList(matchHistory);

        timesPlayedWhite = new HashMap<>();
        for (MatchDto match : matchHistory) {
            if (match.getWhite() != null) {
                String id = match.getWhite().getId();
                timesPlayedWhite.put(id, timesPlayedWhite.getOrDefault(id, 0) + 1);
            }
        }
    }

    public List<ParticipantDto> getRemainingParticipants() {
        return participants.stream().filter(it -> !isBooked(it)).collect(Collectors.toList());
    }

    public boolean isBooked(ParticipantDto participant) {
        return bookedParticipants.contains(participant.getId());
    }

    public int timesPlayedWhite(ParticipantDto participant) {
        return timesPlayedWhite.getOrDefault(participant.getId(), 0);
    }

    public void book(ParticipantDto participant) {
        bookedParticipants.add(participant.getId());
    }

    public long playedTimes(ParticipantDto p1, ParticipantDto p2) {
        return matchHistory.stream()
                .filter(it -> participated(p1, it))
                .filter(it -> participated(p2, it))
                .count();
    }

    public long hadBuy(ParticipantDto participantDto) {
        return matchHistory.stream()
                .filter(it -> isBuyFor(participantDto, it))
                .count();
    }

    private static boolean isBuyFor(ParticipantDto participant, MatchDto match) {
        if (!MatchResult.BUY.equals(match.getResult())) return false;

        return participated(participant, match);
    }

    public static boolean participated(ParticipantDto participant, MatchDto match) {
        ParticipantDto white = match.getWhite();
        ParticipantDto black = match.getBlack();
        return (white != null && white.getId().equals(participant.getId()))
                || (black != null && black.getId().equals(participant.getId()));
    }


}
