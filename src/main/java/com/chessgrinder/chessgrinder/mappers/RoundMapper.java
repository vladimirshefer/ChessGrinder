package com.chessgrinder.chessgrinder.mappers;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import lombok.*;
import org.springframework.stereotype.*;

import static java.util.Comparator.*;

@Component
@RequiredArgsConstructor
public class RoundMapper {

    private final MatchMapper matchMapper;

    public List<RoundDto> toDto(List<RoundEntity> roundEntities) {
        return roundEntities.stream().map(round -> RoundDto.builder()
                        .isFinished(round.isFinished())
                        .number(round.getNumber())
                        .matches(matchMapper.toDto(getSortedMatchEntities(round)))
                        .build())
                .toList();
    }

    //First you need to sort by points (total for the tournament), if they are the same,
    //then sort by nickname
    private List<MatchEntity> getSortedMatchEntities(RoundEntity round) {
        return round.getMatches()
                .stream().sorted(
                        Comparator.<MatchEntity, BigDecimal>comparing(
                                        it -> Optional.ofNullable(it)
                                                .map(m -> {
                                                    BigDecimal score1 = Optional.ofNullable(m.getParticipant1())
                                                            .map(ParticipantEntity::getScore)
                                                            .orElse(BigDecimal.ZERO);
                                                    BigDecimal score2 = Optional.ofNullable(m.getParticipant2())
                                                            .map(ParticipantEntity::getScore)
                                                            .orElse(BigDecimal.ZERO);
                                                    return score1.add(score2);
                                                })
                                                .orElse(BigDecimal.ZERO),
                                        nullsLast(reverseOrder()))
                                .thenComparing(
                                        it -> Optional.ofNullable(it)
                                                .map(MatchEntity::getParticipant1)
                                                .map(ParticipantEntity::getNickname)
                                                .orElse(null),
                                        nullsLast(naturalOrder())
                                )
                                .thenComparing(
                                        it -> Optional.ofNullable(it)
                                                .map(MatchEntity::getParticipant2)
                                                .map(ParticipantEntity::getNickname)
                                                .orElse(null),
                                        nullsLast(naturalOrder())
                                )
                )
                .collect(Collectors.toList());
    }

    public RoundDto toDto(RoundEntity roundEntity) {
        return RoundDto.builder()
                .isFinished(roundEntity.isFinished())
                .number(roundEntity.getNumber())
                .build();
    }
}
