package com.chessgrinder.chessgrinder.chessengine;

import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoundEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.repositories.MatchRepository;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.RoundRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.service.RoundService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.chessgrinder.chessgrinder.util.DateUtil.nowInstantAtUtc;
import static org.assertj.core.api.Assertions.assertThat;

//@DataJpaTest -- мб надо вставить, если репозитории не сработают!
public class WinningCyclePlacingTest {

    private RoundService roundService;
    private ParticipantRepository participantRepository;
    private TournamentRepository tournamentRepository;
    private RoundRepository roundRepository;
    private MatchRepository matchRepository;


    @Test
    void shouldCorrectlySetFinalPlacements() {
        //TODO хочется использовать существующие create...() методы, чтобы код не дублировать
        //TODO всё проверить, скорее всего, не будет работать
        TournamentEntity tournament = tournamentRepository.save(createTournament());

        ParticipantEntity p1 = participantRepository.save(createParticipant("Миша буги вуги", tournament, 21.5, 5));
        ParticipantEntity p2 = participantRepository.save(createParticipant("Непомерное свинство", tournament, 20, 5));
        ParticipantEntity p3 = participantRepository.save(createParticipant("In chess we trust", tournament, 18, 5));
        ParticipantEntity p4 = participantRepository.save(createParticipant("The semen arsonist", tournament, 19, 4));
        ParticipantEntity p5 = participantRepository.save(createParticipant("Alex", tournament, 20.5, 3));
        ParticipantEntity p6 = participantRepository.save(createParticipant("DmitryAnikeyev", tournament, 20, 3));
        ParticipantEntity p7 = participantRepository.save(createParticipant("Волк не тот", tournament, 17, 3));
        ParticipantEntity p8 = participantRepository.save(createParticipant("магадан", tournament, 14, 3));
        ParticipantEntity p9 = participantRepository.save(createParticipant("Султан", tournament, 9.5, 2.5));
        ParticipantEntity p10 = participantRepository.save(createParticipant("сэр А'ртур", tournament, 14.5, 2));
        ParticipantEntity p11 = participantRepository.save(createParticipant("Bob", tournament, 16.5, 1.5));
        ParticipantEntity p12 = participantRepository.save(createParticipant("milpops", tournament, 9, 1));
        ParticipantEntity p13 = participantRepository.save(createParticipant("если проиграю то мощно", tournament, 7, 1));
        ParticipantEntity p14 = participantRepository.save(createParticipant("Страх и ненависть на шахматах", tournament, 11, 0));

        RoundEntity round1 = roundRepository.save(createRound(tournament, 1));
        RoundEntity round2 = roundRepository.save(createRound(tournament, 2));
        RoundEntity round3 = roundRepository.save(createRound(tournament, 3));
        RoundEntity round4 = roundRepository.save(createRound(tournament, 4));
        RoundEntity round5 = roundRepository.save(createRound(tournament, 5));
        RoundEntity round6 = roundRepository.save(createRound(tournament, 6));

        //Пейринги нужны для того чтобы расставить места согласно личным встречам!
        //TODO исправить: есть неправильные пейринги! (начиная со 2 тура)
        //1 round
        matchRepository.save(createMatch(p1, p11, round1, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p6, p2, round1, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p14, p3, round1, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p4, p13, round1, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p5, p7, round1, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p8, p12, round1, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p10, null, round1, MatchResult.BUY));
        //2 round
        matchRepository.save(createMatch(p3, p1, round2, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p2, p14, round2, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p10, p4, round2, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p12, p5, round2, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p13, p7, round2, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p7, p8, round2, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p11, p9, round2, MatchResult.DRAW));
        //3 round
        matchRepository.save(createMatch(p5, p1, round3, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p4, p2, round3, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p3, p10, round3, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p6, p12, round3, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p9, p7, round3, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p8, p11, round3, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p14, p13, round3, MatchResult.BLACK_WIN));
        //4 round
        matchRepository.save(createMatch(p1, p2, round4, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p5, p3, round4, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p8, p4, round4, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p7, p6, round4, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p10, p9, round4, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p11, null, round4, MatchResult.BUY));
        matchRepository.save(createMatch(p12, null, round4, MatchResult.MISS));
        matchRepository.save(createMatch(p13, null, round4, MatchResult.MISS));
        matchRepository.save(createMatch(p14, null, round4, MatchResult.MISS));
        //5 round
        matchRepository.save(createMatch(p1, p6, round5, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p2, p3, round5, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p7, p4, round5, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p11, p5, round5, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p9, p8, round5, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p10, null, round5, MatchResult.BUY));
        matchRepository.save(createMatch(p12, null, round5, MatchResult.MISS));
        matchRepository.save(createMatch(p13, null, round5, MatchResult.MISS));
        matchRepository.save(createMatch(p14, null, round5, MatchResult.MISS));
        //6 round
        matchRepository.save(createMatch(p4, p1, round6, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p2, p5, round6, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p6, p3, round6, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p7, p8, round6, MatchResult.BLACK_WIN));
        matchRepository.save(createMatch(p11, p10, round6, MatchResult.WHITE_WIN));
        matchRepository.save(createMatch(p9, null, round6, MatchResult.BUY));
        matchRepository.save(createMatch(p12, null, round6, MatchResult.MISS));
        matchRepository.save(createMatch(p13, null, round6, MatchResult.MISS));
        matchRepository.save(createMatch(p14, null, round6, MatchResult.MISS));
        //Triggering recalculation method
        //TODO кажется, надо вызывать каждый тур для пересчета очков?
        //Возможно, нет, т.к. важны итоговые очки и личные встречи!
        roundService.finishRound(tournament.getId(), 6);

        List<ParticipantEntity> sorted = participantRepository.findByTournamentId(tournament.getId()).stream()
                .sorted(Comparator.comparing(ParticipantEntity::getPlace))
                .toList();

        assertThat(sorted.get(0).getNickname()).isEqualTo("Миша буги вуги");
        assertThat(sorted.get(1).getNickname()).isEqualTo("Непомерное свинство");
        assertThat(sorted.get(2).getNickname()).isEqualTo("In chess we trust");
        assertThat(sorted.get(3).getNickname()).isEqualTo("The semen arsonist");
        assertThat(sorted.get(4).getNickname()).isEqualTo("Alex");
        assertThat(sorted.get(5).getNickname()).isEqualTo("DmitryAnikeyev");
        assertThat(sorted.get(6).getNickname()).isEqualTo("Волк не тот");
        assertThat(sorted.get(7).getNickname()).isEqualTo("магадан");
        assertThat(sorted.get(8).getNickname()).isEqualTo("Султан");
        assertThat(sorted.get(9).getNickname()).isEqualTo("сэр А'ртур");
        assertThat(sorted.get(10).getNickname()).isEqualTo("Bob");
        assertThat(sorted.get(11).getNickname()).isEqualTo("milpops");
        assertThat(sorted.get(12).getNickname()).isEqualTo("если проиграю то мощно");
        assertThat(sorted.get(13).getNickname()).isEqualTo("Страх и ненависть на шахматах");
    }

    private TournamentEntity createTournament() {
        return TournamentEntity.builder()
                .id(UUID.randomUUID())
                .status(TournamentStatus.ACTIVE)
                .name("Test Tournament")
                .roundsNumber(6)
                //.date(LocalDateTime.ofInstant(nowInstantAtUtc(), ZoneOffset.UTC))
                .pairingStrategy("SWISS")
                .build();
    }

    private ParticipantEntity createParticipant(String name, TournamentEntity tournament, double buchholz, double score) {
        return ParticipantEntity.builder()
                .id(UUID.randomUUID())
                .nickname(name)
                .score(BigDecimal.valueOf(score))
                .buchholz(BigDecimal.valueOf(buchholz))
                .tournament(tournament)
                .isMissing(false)
                .place(-1)
                .build();
    }

    private RoundEntity createRound(TournamentEntity tournament, int number) {
        return RoundEntity.builder()
                .id(UUID.randomUUID())
                .number(number)
                .isFinished(false)
                .tournament(tournament)
                .build();
    }

    private MatchEntity createMatch(ParticipantEntity p1, ParticipantEntity p2, RoundEntity round, MatchResult result) {
        return MatchEntity.builder()
                .id(UUID.randomUUID())
                .participant1(p1)
                .participant2(p2)
                .round(round)
                .result(result)
                .build();
    }
}
