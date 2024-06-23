package com.chessgrinder.chessgrinder.mappers;

import com.chessgrinder.chessgrinder.dto.RoundDto;
import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoundEntity;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Comparator.*;

@Component
@RequiredArgsConstructor
public class RoundMapper {

    private final MatchMapper matchMapper;

    /**
     * First send down all non-playable pairings (MISS and BYE),
     * Then sort by points (total for the tournament), if they are the same,
     * Then sort by nickname,
     */
    private final Comparator<MatchEntity> MATCH_COMPARATOR = Comparator
            .<MatchEntity, BigDecimal>comparing(
                    it -> Optional.ofNullable(it)
                            .map(MatchEntity::getResult)
                            .map(result -> switch (result) {
                                case MISS -> BigDecimal.valueOf(2); // MISS is in the end
                                case BUY -> BigDecimal.ONE; // BYE is after all meaningful results
                                default -> BigDecimal.ZERO; // all other results (e.g. WHITE_WIN, DRAW, unknown, etc.)
                            })
                            .orElse(BigDecimal.valueOf(3)),
                    nullsLast(naturalOrder())
            )
            .thenComparing(
                    it -> Optional.ofNullable(it)
                            .map(m -> getScoreOrZero(m.getParticipant1()).add(getScoreOrZero(m.getParticipant2())))
                            .orElse(BigDecimal.ZERO),
                    nullsLast(reverseOrder())
            )
            .thenComparing(
                    it -> Optional.ofNullable(it)
                            .map(MatchEntity::getParticipant1).map(ParticipantEntity::getNickname)
                            .orElse(null),
                    nullsLast(naturalOrder())
            )
            .thenComparing(
                    it -> Optional.ofNullable(it)
                            .map(MatchEntity::getParticipant2).map(ParticipantEntity::getNickname)
                            .orElse(null),
                    nullsLast(naturalOrder())
            );

    public List<RoundDto> toDto(List<RoundEntity> roundEntities) {
        return roundEntities.stream().map(round -> RoundDto.builder().isFinished(round.isFinished()).number(round.getNumber()).matches(matchMapper.toDto(getSortedMatchEntities(round))).build()).toList();
    }

    private List<MatchEntity> getSortedMatchEntities(RoundEntity round) {
        return round.getMatches().stream().sorted(MATCH_COMPARATOR).collect(Collectors.toList());
    }

    @Nonnull
    private static BigDecimal getScoreOrZero(ParticipantEntity participant2) {
        return Optional.ofNullable(participant2).map(ParticipantEntity::getScore).orElse(BigDecimal.ZERO);
    }

    public RoundDto toDto(RoundEntity roundEntity) {
        return RoundDto.builder().isFinished(roundEntity.isFinished()).number(roundEntity.getNumber()).build();
    }
}
