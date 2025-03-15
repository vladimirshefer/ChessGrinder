package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.chessengine.pairings.JavafoPairingStrategyImpl;
import com.chessgrinder.chessgrinder.chessengine.pairings.PairingStrategy;
import com.chessgrinder.chessgrinder.chessengine.pairings.RoundRobinPairingStrategyImpl;
import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.repositories.MatchRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.chessgrinder.chessgrinder.chessengine.MockTournamentRunnerUtils.participant;
import static com.chessgrinder.chessgrinder.chessengine.MockTournamentRunnerUtils.runTournament;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class StatsAgainstUserTest {

    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private MatchRepository matchRepository;

    private List<MatchEntity> matches;

    private UUID userId1;
    private UUID userId2;
    private UUID userId3;
    private UUID userId4;
    private UUID finishedTournamentId1;
    private UUID finishedTournamentId2;
    private UUID unfinishedTournamentId;

    PairingStrategy pairingEngine = new RoundRobinPairingStrategyImpl();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
        userId3 = UUID.randomUUID();
        userId4 = UUID.randomUUID();

        finishedTournamentId1 = UUID.randomUUID();
        finishedTournamentId2 = UUID.randomUUID();
        unfinishedTournamentId = UUID.randomUUID();

        var t1 = TournamentEntity.builder().id(finishedTournamentId1).status(TournamentStatus.FINISHED).build();
        var t2 = TournamentEntity.builder().id(finishedTournamentId2).status(TournamentStatus.FINISHED).build();
        var t3 = TournamentEntity.builder().id(unfinishedTournamentId).status(TournamentStatus.ACTIVE).build();

        List<TournamentEntity> allTournaments = List.of(t1, t2, t3);
        when(tournamentRepository.findAll()).thenReturn(allTournaments);

        matches = generateMatches();
        when(matchRepository.findAll()).thenReturn(matches);

        var mockTournamentRunner = runTournament(pairingEngine, "user1", "user2", "user3", "user4");
        mockTournamentRunner
                .thenRound(round -> round
                        //TODO в этого participant надо вставлять userId, чтобы по нему был расчет!
                        .match(participant("user1"), participant("user3"), MatchResult.WHITE_WIN)
                        .match(participant("user2"), null, MatchResult.BUY)
                )
                .thenRound(round -> round
                        .match(participant("user2"), participant("user1"), MatchResult.WHITE_WIN)
                        .match(participant("user3"), null, MatchResult.BUY)
                )
                .thenRound(round -> round
                        .match(participant("user3"), participant("user2"), MatchResult.DRAW)
                        .match(participant("user1"), null, MatchResult.BUY)
                )
                .thenRound(round -> round
                        .match(participant("user1"), participant("user3"), MatchResult.WHITE_WIN)
                        .match(participant("user2"), null, MatchResult.BUY)
                )
                .thenRound(round -> round
                        .match(participant("user2"), participant("user1"), MatchResult.BLACK_WIN)
                        .match(participant("user3"), null, MatchResult.BUY)
                )
                .thenRound(round -> round
                        .match(participant("user3"), participant("user2"), MatchResult.BLACK_WIN)
                        .match(participant("user1"), null, MatchResult.BUY)
                )
                /*.calcPoints()*/; //TODO доделать подсчет очков
    }

    private List<MatchEntity> generateMatches() {
        List<MatchEntity> matches = new ArrayList<>();
        for (UUID tournamentId : List.of(finishedTournamentId1, finishedTournamentId2, unfinishedTournamentId)) {
            for (int i = 0; i < 6; i++) { // 6 rounds
                //TODO думаю, надо хранить не id пользователей, а самих пользователей, а точнее, участников с привязанными
                //пользователями!
                //Участников может быть очень много (в данном случае, 12, а кол-во пользователей всегда одно и то же)
                //Тогда надо хранить именно UserEntity
                //Также хочу, чтобы очки участников считались автоматически, из пейринга
                //MatchEntity.builder().participant1().result(MatchResult.WHITE_WIN);

                //matches.add(new MatchEntity(player1, player2, "WHITE_WIN", tournamentId));
                //matches.add(new MatchEntity(player3, player4, "BLACK_WIN", tournamentId));
                //matches.add(new MatchEntity(player1, player3, "DRAW", tournamentId));
            }
        }
        return matches;
    }

    //TODO сначала надо создать структуру (либо использовать существующую, где-то)
    //Умар использовал существующие методы, а мне надо где-то хранить это всё, наверно, в
    //моковых репозиториях
    //В турниры надо добавить несколько участников без привязанного пользователя
    //TODO надо посмотреть, есть ли где-нибудь готовые турниры, чтобы самому с нуля не создавать
    //Основные претенденты: JavafoPairingStrategyImplTest
    //Там есть несколько методов: MockTournamentRunner runTournament() и весь класс
    // public class MockTournamentRunnerUtils
    //Получается, надо модифицировать MockTournamentRunner под свои нужды (иниц. его в конструкторе)
    @Test
    public void testWithZeroMatches() {

    }

    @Test
    public void testWithUsualConditions() {

    }

    @Test
    public void testWithUnfinishedTournaments() {

    }
}
