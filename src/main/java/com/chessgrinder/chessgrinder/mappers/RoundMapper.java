package com.chessgrinder.chessgrinder.mappers;

import java.util.*;
import java.util.stream.Collectors;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import lombok.*;
import org.springframework.stereotype.*;

import static java.util.Comparator.*;

@Component
@RequiredArgsConstructor
public class RoundMapper {

    private final MatchMapper matchMapper;
    private List<RoundEntity> roundEntities;

    public List<RoundDto> toDto(List<RoundEntity> roundEntities) {
        this.roundEntities = roundEntities;
        //TODO думаю, можно один раз создать эту структуру и передавать ее в структуру ниже по частям
        //Ф-ия map преобразует каждый объект коллекции по заданным правилам (из RoundEntity в RoundDto)
        //Map<round's index, round> -- хотя думаю, в таком случае можно просто round хранить: в нем же есть номер
        List<RoundEntity> lst = getRoundDtos(roundEntities);
//        return lst.stream().map(round -> RoundDto.builder()
//                .isFinished(round.isFinished())
//                .number(round.getNumber())
//                .matches(matchMapper.toDto(round.getMatches()))
//                .build()
//        ).toList();


        //TODO сделать так: сортировать по очкам, если очки равны, сортировать по алфавиту
        // (на случай первого тура, когда у всех по нулям)
        return roundEntities.stream().map(round -> RoundDto.builder()
                        .isFinished(round.isFinished())
                        .number(round.getNumber())
                        .matches(matchMapper.toDto(getMatchEntities(round)))
                        .build())
                .toList();
    }

    private List<RoundEntity> getRoundDtos(List<RoundEntity> roundEntities) {
        List<RoundEntity> result = new ArrayList<>();
        final int size = roundEntities.size();
        if (size == 0) {
            return result;
        }
        final var firstRoundMatches = roundEntities.get(0).getMatches();
//        Key - participant's id, value - linked list of points
//        List contains number of points at the beginning of the rounds (in the first round everything is zero)
//        The last cell contains the result of the entire tournament to avoid indexing errors
        Map<UUID, List<Double>> pointsPerRoundMap = new HashMap<>(firstRoundMatches.size() * 2);

        //Initialization - searching for participants
        //It must be taken into account that one participant may be null if there is a BUY
        for (final var match : firstRoundMatches) {
            final var first = match.getParticipant1();
            final var second = match.getParticipant2();
            if (first != null) {
                pointsPerRoundMap.put(first.getId(), new ArrayList<>(Collections.nCopies(size + 1, 0.)));
            }
            if (second != null) {
                pointsPerRoundMap.put(second.getId(), new ArrayList<>(Collections.nCopies(size + 1, 0.)));
            }
        }

        //TODO подумать (обсудить с Владимиром), можно ли создать единую систему присуждения очков
        //(чтобы все менять в одном месте, если надо будет менять, например, BUY)

        //Lists are filled in by induction
        for (int roundIndex = 0; roundIndex < size; ++roundIndex) {
            final RoundEntity currentRound = roundEntities.get(roundIndex);
            for (final var match : currentRound.getMatches()) {
                final var first = match.getParticipant1();
                final var second = match.getParticipant2();
                if (match.getResult() == MatchResult.WHITE_WIN) {
                    var firstList = pointsPerRoundMap.get(first.getId());
                    final var firstCurrentScore = firstList.get(roundIndex);
                    firstList.set(roundIndex + 1, firstCurrentScore + 1);
                    var secondList = pointsPerRoundMap.get(second.getId());
                    final var secondCurrentScore = secondList.get(roundIndex);
                    secondList.set(roundIndex + 1, secondCurrentScore);
                }
                else if (match.getResult() == MatchResult.BLACK_WIN) {
                    var firstList = pointsPerRoundMap.get(first.getId());
                    final var firstCurrentScore = firstList.get(roundIndex);
                    firstList.set(roundIndex + 1, firstCurrentScore);
                    var secondList = pointsPerRoundMap.get(second.getId());
                    final var secondCurrentScore = secondList.get(roundIndex);
                    secondList.set(roundIndex + 1, secondCurrentScore + 1);
                }
                else if (match.getResult() == MatchResult.DRAW) {
                    var firstList = pointsPerRoundMap.get(first.getId());
                    final var firstCurrentScore = firstList.get(roundIndex);
                    firstList.set(roundIndex + 1, firstCurrentScore + 0.5);
                    var secondList = pointsPerRoundMap.get(second.getId());
                    final var secondCurrentScore = secondList.get(roundIndex);
                    secondList.set(roundIndex + 1, secondCurrentScore + 0.5);
                }
                else if (match.getResult() == MatchResult.BUY) {
                    var list = pointsPerRoundMap.get((first != null ? first : second).getId());
                    final var currentScore = list.get(roundIndex);
                    list.set(roundIndex + 1, currentScore + 1);
                }
            }
        }
        return result;
    }

    private List<MatchEntity> getMatchEntities(RoundEntity round) {
        List<MatchEntity> result = round.getMatches();
        // TODO См. в чат гпт "Чтобы сначала отсортировать по сумме очков двух участников"
        // В методе (public void updateResults(UUID tournamentId)) рассчитываются очки
        //Есть еще public void calculate()  - это все упоминания прибавления очков в программе
        return round.getMatches()
                .stream().sorted(
                        Comparator.<MatchEntity, String> comparing(
                                        it -> Optional.ofNullable(it)
                                                .map(MatchEntity::getParticipant1)
                                                .map(ParticipantEntity::getNickname)
                                                .orElse(null),
                                        nullsLast(naturalOrder()))
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
