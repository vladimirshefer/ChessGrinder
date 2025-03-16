package com.chessgrinder.chessgrinder.chessengine;

import com.chessgrinder.chessgrinder.chessengine.pairings.PairingStrategy;
import com.chessgrinder.chessgrinder.dto.MatchDto;
import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import jakarta.annotation.Nullable;

import java.math.BigDecimal;

public class MockTournamentRunnerUtils {

    public static ParticipantDto participant(String name, double score, double buchholz) {
        if (name == null) return null;
        return ParticipantDto.builder()
                .id(name)
                .name(name)
                .score(BigDecimal.valueOf(score))
                .buchholz(BigDecimal.valueOf(buchholz))
                .place(-1)
                .isMissing(false)
                .build();
    }

    public static MatchDto createMatch(@Nullable ParticipantDto white, @Nullable ParticipantDto black, @Nullable MatchResult result) {
        return MatchDto.builder()
                .white(white)
                .black(black)
                .result(result)
                .build();
    }

    public static MockTournamentRunner runTournament(PairingStrategy swissEngine, String... participants) {
        return new MockTournamentRunner(swissEngine, participants);
    }
}

