package com.chessgrinder.chessgrinder.mappers;

import com.chessgrinder.chessgrinder.dto.RoundDto;
import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.RoundEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.chessgrinder.chessgrinder.comparator.MatchEntityComparators.*;

@Component
@RequiredArgsConstructor
public class RoundMapper {

    private final MatchMapper matchMapper;

    /**
     * First send down all non-playable pairings (MISS and BYE),
     * Then if applicable sort by place of the best player in pair,
     * Then sort by points (total for the tournament), if they are the same,
     * Then sort by nickname,
     */
    private final Comparator<MatchEntity> COMPARE_MATCH_ENTITY_IN_ROUND = COMPARE_MATCH_ENTITY_BY_RESULT_MEANINGFUL_FIRST
            .thenComparing(COMPARE_MATCH_ENTITY_BY_PLAYER_SCORE_AND_BUCHHOLZ_NULLS_LAST)
            .thenComparing(COMPARE_MATCH_ENTITY_BY_NICKNAME1)
            .thenComparing(COMPARE_MATCH_ENTITY_BY_NICKNAME2);

    public List<RoundDto> toDto(List<RoundEntity> roundEntities) {
        return roundEntities.stream()
                .map(round -> RoundDto
                        .builder()
                        .isFinished(round.isFinished())
                        .number(round.getNumber())
                        .matches(matchMapper.toDto(getSortedMatchEntities(round)))
                        .build()
                )
                .toList();
    }

    private List<MatchEntity> getSortedMatchEntities(RoundEntity round) {
        return round.getMatches().stream()
                .sorted(COMPARE_MATCH_ENTITY_IN_ROUND)
                .collect(Collectors.toList());
    }

    public RoundDto toDto(RoundEntity roundEntity) {
        return RoundDto.builder().isFinished(roundEntity.isFinished()).number(roundEntity.getNumber()).build();
    }
}
