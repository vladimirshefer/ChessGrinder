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
//        <123..., {1, 2, 3, 4, 5, 6}>
        final var firstRoundMatches = roundEntities.get(0).getMatches();
        //В list находятся очки при самом запуске тура, т.е. в первом раунде всё по нулям
        //На начало второго тура уже есть очки за первый тур и т.д.
        //На начало последнего тура находится сумма всех предыдущих туров (не текущего)
        //Количество очков на начало туров
        //В последнюю ячейку закладывается результат всего турнира во избежание ошибки индексации
        Map<UUID, List<Double>> pointsPerRoundMap = new HashMap<>(firstRoundMatches.size() * 2);

        //Initialization - searching for participants
        //Нужно учитывать, что один участник может быть null, если будет BUY
        //Неизвестно, могут ли белые быть null, поэтому надо проверять всех
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

        //TODO узнать почему pointsPerRoundMap заполняется неправильно (не работает по индукции)
        //У меня есть предположение, что этот список надо заполнять и при поражении, а не только при победе
        //Теперь новая проблема: т.к. итерация начинается с 1, неизвестны результаты первого тура
        //Предположенное решение: закладывать результаты в i+1 ячейку
        //TODO подумать (обсудить с Владимиром), можно ли создать единую систему присуждения очков
        //(чтобы все менять в одном месте, если надо будет менять, например, BUY)
        for (int roundIndex = 0; roundIndex < size; ++roundIndex) {
            final RoundEntity currentRound = roundEntities.get(roundIndex);
            for (final var match : currentRound.getMatches()) {
                final var first = match.getParticipant1();
                final var second = match.getParticipant2();
                if (match.getResult() == MatchResult.WHITE_WIN) {
                    var firstList = pointsPerRoundMap.get(first.getId());
                    final var firstCurrentScore = firstList.get(roundIndex);
                    var secondList = pointsPerRoundMap.get(second.getId());
                    final var secondCurrentScore = secondList.get(roundIndex);
//                    if (roundIndex != size) {
                        firstList.set(roundIndex + 1, firstCurrentScore + 1);
                        secondList.set(roundIndex + 1, secondCurrentScore);
//                    }
                }
                else if (match.getResult() == MatchResult.BLACK_WIN) {
                    var firstList = pointsPerRoundMap.get(first.getId());
                    final var firstCurrentScore = firstList.get(roundIndex);
                    var secondList = pointsPerRoundMap.get(second.getId());
                    final var secondCurrentScore = secondList.get(roundIndex);
//                    if (roundIndex != size) {
                        firstList.set(roundIndex + 1, firstCurrentScore);
                        secondList.set(roundIndex + 1, secondCurrentScore + 1);
//                    }
                }
                else if (match.getResult() == MatchResult.DRAW) {
                    var firstList = pointsPerRoundMap.get(first.getId());
                    final var firstCurrentScore = firstList.get(roundIndex);
                    var secondList = pointsPerRoundMap.get(second.getId());
                    final var secondCurrentScore = secondList.get(roundIndex);
//                    if (roundIndex != size) {
                        firstList.set(roundIndex + 1, firstCurrentScore + 0.5);
                        secondList.set(roundIndex + 1, secondCurrentScore + 0.5);
//                    }
                }
                //Если это BUY, тогда один из участников может быть null!
                //И тогда 1 очко присуждается тому, кто не нулл
                else if (match.getResult() == MatchResult.BUY) {
                    List<Double> list = pointsPerRoundMap.get((first != null ? first : second).getId());
                    final var currentScore = list.get(roundIndex);
//                    if (roundIndex != size) {
                        list.set(roundIndex + 1, currentScore + 1);
//                    }
                }
                else {
                    //Вылезает это исключение, т.к. результата может не быть (это ?-?)
//                    throw new RuntimeException("Unchecked MatchResult enum");
                }
            }
        }
        return result;
    }

    private List<MatchEntity> getMatchEntities(RoundEntity round) {
        List<MatchEntity> result = round.getMatches();
//        round - это i-ая сущность списка туров (round.getNumber)
//        Нужны также туры, которые стоят до этого тура, чтобы подсчитать очки
        //Т.е. нужно как-то передать tournamentRoundEntities двумя методами в иерархии выше
//        Хотя в принципе хватит и roundEntities
//        В моем примере в одном раунде 3 пары (matches)
        //Для оптимизации это всё можно посчитать один раз (в последний)
        //Нужно сформировать карту для каждого пользователя по турам (Что-то типа Map<String, Set<String>>)
        //Ключ - id участника, значение - связный список баллов

        //См. в чат гпт "Чтобы сначала отсортировать по сумме очков двух участников"
        //TODO нужно сравнивать не по сумме очков вообще, а только с учетом предыдущих туров
        //Проблема: каждый участник хранит в себе сумму очков вообще за все туры, а не за конкретные
        //TODO узнать что происходит после POST /finish (public void updateResults(UUID tournamentId))
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
