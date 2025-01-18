package com.chessgrinder.chessgrinder.chessengine.ratings;

import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import jakarta.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.chessgrinder.chessgrinder.service.UserEloInitializerService.DEFAULT_ELO_POINTS;

public class EloHolder {
    private final Map<UUID, Integer> participantId2InitialElo = new HashMap<>();
    private final Map<UUID, Integer> participantId2ResultElo = new HashMap<>();

    public void putInitial(UUID participantId, int elo) {
        participantId2InitialElo.put(participantId, elo);
    }

    public void putResult(UUID participantId, int elo) {
        participantId2ResultElo.put(participantId, elo);
    }

    public int getInitial(UUID participantId) {
        return participantId2InitialElo.getOrDefault(participantId, DEFAULT_ELO_POINTS);
    }

    public int getDiff(UUID participantId) {
        Integer result = getResult(participantId);
        if (result == null) throw new IllegalArgumentException("Result is null for participant: " + participantId);
        return result - getInitial(participantId);
    }

    @Nullable
    public Integer getResult(UUID participantId) {
        return participantId2ResultElo.get(participantId);
    }


    public Integer getResultOr(UUID participantId, int defaultEloPoints) {
        Integer orDefault = this.getResult(participantId);
        if (orDefault == null || orDefault == 0) return defaultEloPoints;
        else return orDefault;
    }

    public void putInitial(ParticipantEntity participant) {
        if (participant == null) {
            return;
        }

        UUID pid = participant.getId();
        if (participant.getInitialEloPoints() != 0) {
            putInitial(pid, participant.getInitialEloPoints());
        } else if (participant.getUser() != null && participant.getUser().getEloPoints() != 0) {
            putInitial(pid, participant.getUser().getEloPoints());
        } else {
            putInitial(pid, DEFAULT_ELO_POINTS);
        }
    }

    public Map<UUID, Integer> getInitial() {
        return participantId2InitialElo;
    }

    public Map<UUID, Integer> getResult() {
        return participantId2ResultElo;
    }
}
