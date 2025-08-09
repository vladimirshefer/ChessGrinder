package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.service.TournamentService.TournamentListener;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.chessgrinder.chessgrinder.service.ReputationTournamentListenerImpl.MatchUtils.getUserId;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReputationTournamentListenerImpl implements TournamentListener {
    private final UserRepository userRepository;

    @Override
    public void totalReset() {
        userRepository.clearAllReputation();
    }

    @Override
    @Transactional
    public void tournamentFinished(TournamentEntity tournamentEntity) {
        Map<UUID, Integer> result = calculateReputationDiff(tournamentEntity);

        //noinspection Convert2MethodRef
        result.forEach((userId, amount) -> userRepository.addReputation(userId, amount));
    }

    private static Map<UUID, Integer> calculateReputationDiff(TournamentEntity tournamentEntity) {
        Map<UUID, BigDecimal> result = new HashMap<>();
        for (RoundEntity roundEntity : tournamentEntity.getRounds()) {
            for (MatchEntity matchEntity : roundEntity.getMatches()) {
                add(result, getUserId(matchEntity.getParticipant1()), calculateForParticipant(matchEntity, matchEntity.getParticipant1()));
                add(result, getUserId(matchEntity.getParticipant2()), calculateForParticipant(matchEntity, matchEntity.getParticipant2()));
            }
        }

        return result.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                it -> it.getValue().intValue()
        ));
    }

    private static BigDecimal calculateForParticipant(@Nullable MatchEntity match, @Nullable ParticipantEntity participant) {
        if (match == null || participant == null) {
            return BigDecimal.ZERO;
        }
        UserEntity user = participant.getUser();
        if (user == null) {
            return BigDecimal.ZERO;
        }

        if (MatchUtils.isBuyFor(match, participant)) {
            return BigDecimal.valueOf(1.0);
        } else if (MatchUtils.isWinner(match, participant)) {
            return BigDecimal.valueOf(1.2);
        } else if (MatchUtils.isLoser(match, participant)) {
            return BigDecimal.valueOf(0.8);
        } else if (MatchUtils.isDrawFor(match, participant)) {
            return BigDecimal.valueOf(1.0);
        } else {
            return BigDecimal.ZERO;
        }

    }

    private static <K> void add(Map<K, BigDecimal> map, @Nullable K key, BigDecimal value) {
        if (key == null) {
            return;
        }
        if (map.containsKey(key)) {
            map.put(key, map.get(key).add(value));
        } else {
            map.put(key, value);
        }
    }

    @Override
    @Transactional
    public void tournamentReopened(TournamentEntity tournamentEntity) {
        Map<UUID, Integer> result = calculateReputationDiff(tournamentEntity);
        result.forEach((userId, amount) -> userRepository.addReputation(userId, -amount));
    }

    public static class MatchUtils {
        @Nullable
        public static ParticipantEntity getWinner(MatchEntity match) {
            if (match.getResult() == MatchResult.WHITE_WIN) return match.getParticipant1();
            if (match.getResult() == MatchResult.BLACK_WIN) return match.getParticipant2();
            if (match.getResult() == MatchResult.BUY) {
                if (match.getParticipant1() != null) return match.getParticipant1();
                if (match.getParticipant2() != null) return match.getParticipant2();
            }
            return null;
        }

        @Nullable
        public static ParticipantEntity getLoser(MatchEntity match) {
            if (match.getResult() == MatchResult.WHITE_WIN) return match.getParticipant2();
            if (match.getResult() == MatchResult.BLACK_WIN) return match.getParticipant1();
            return null;
        }

        public static boolean isWinner(MatchEntity match, ParticipantEntity participant) {
            ParticipantEntity winner = getWinner(match);
            if (winner == null) {
                return false;
            }
            return winner.equals(participant);
        }

        public static boolean isLoser(MatchEntity match, ParticipantEntity participant) {
            ParticipantEntity loser = getLoser(match);
            if (loser == null) {
                return false;
            }
            return loser.equals(participant);
        }

        public static boolean isParticipant(MatchEntity match, ParticipantEntity participant) {
            if (participant == null) return false;
            return Objects.equals(match.getParticipant1(), participant) || Objects.equals(match.getParticipant2(), participant);
        }

        public static boolean isDrawFor(MatchEntity match, ParticipantEntity participant) {
            return match.getResult() == MatchResult.DRAW && isParticipant(match, participant);
        }

        public static boolean isBuyFor(MatchEntity match, ParticipantEntity participant) {
            return match.getResult() == MatchResult.BUY && isParticipant(match, participant);
        }

        @Nullable
        public static UUID getUserId(ParticipantEntity participant) {
            if (participant == null) return null;
            if (participant.getUser() == null) return null;
            return participant.getUser().getId();
        }
    }
}
