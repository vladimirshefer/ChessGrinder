package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.EloUpdateResultDto;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.SecurityUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class EloServiceImplTest {

    private EloServiceImpl eloService;
    private UserEloInitializerService userEloInitializerService;
    private DefaultEloCalculationStrategy defaultEloCalculationStrategy;
    private ParticipantRepository participantRepository;
    private TournamentRepository tournamentRepository;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userRepository = new InMemoryUserRepository();
        userEloInitializerService = new UserEloInitializerService(userRepository);

        defaultEloCalculationStrategy = new DefaultEloCalculationStrategy() {
            @Override
            public EloUpdateResultDto calculateElo(int whiteElo, int blackElo, MatchResult result, boolean bothUsersAuthorized) {
                if (result == MatchResult.DRAW) {
                    return EloUpdateResultDto.builder()
                            .whiteNewElo(whiteElo)
                            .blackNewElo(blackElo)
                            .build();
                }
                int points = bothUsersAuthorized ? 10 : 5;
                int playerNewElo = result == MatchResult.WHITE_WIN ? whiteElo + points : whiteElo - points;
                int opponentNewElo = result == MatchResult.WHITE_WIN ? blackElo - points : blackElo + points;
                return EloUpdateResultDto.builder()
                        .whiteNewElo(playerNewElo)
                        .blackNewElo(opponentNewElo)
                        .build();
            }
        };

        participantRepository = new InMemoryParticipantRepository();
        tournamentRepository = new InMemoryTournamentRepository();
        userRepository = new InMemoryUserRepository();

        eloService = new EloServiceImpl(userEloInitializerService, defaultEloCalculationStrategy, participantRepository,userRepository, tournamentRepository);
        ReflectionTestUtils.setField(eloService, "eloServiceEnabled", true);
    }

    @Test
    public void testTwoAuthorizedUsersMatch() {
        UserEntity user1 = new UserEntity();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setEloPoints(1200);

        UserEntity user2 = new UserEntity();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setEloPoints(1200);


        ParticipantEntity participant1 = new ParticipantEntity();
        participant1.setId(UUID.randomUUID());
        participant1.setUser(user1);

        ParticipantEntity participant2 = new ParticipantEntity();
        participant2.setId(UUID.randomUUID());
        participant2.setUser(user2);

        MatchEntity match = new MatchEntity();
        match.setParticipant1(participant1);
        match.setParticipant2(participant2);
        match.setResult(MatchResult.WHITE_WIN);


        RoundEntity round = new RoundEntity();
        round.setMatches(List.of(match));
        round.setFinished(true); // помечаем раунд как завершенный

        // Создаем турнир и добавляем раунд
        TournamentEntity tournament = new TournamentEntity();
        tournament.setId(UUID.randomUUID());
        tournament.setName("Test Tournament");
        tournament.setRounds(List.of(round)); // добавляем раунд в турнир
        tournament.setStatus(TournamentStatus.ACTIVE);


        eloService.processTournamentAndUpdateElo(tournament);

        assertEquals(1210, user1.getEloPoints());
        assertEquals(1190, user2.getEloPoints());
    }

    @Test
    public void testEloRecalculationAfterMatchResultChange() {
        // Step 1: Создаем двух пользователей
        UserEntity user1 = new UserEntity();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setEloPoints(1200);

        UserEntity user2 = new UserEntity();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setEloPoints(1200);

        // Step 2: Создаем участников
        ParticipantEntity participant1 = new ParticipantEntity();
        participant1.setId(UUID.randomUUID());
        participant1.setUser(user1);


        ParticipantEntity participant2 = new ParticipantEntity();
        participant2.setId(UUID.randomUUID());
        participant2.setUser(user2);


        // Step 3: Создаем матч с результатом WHITE_WIN
        MatchEntity match = new MatchEntity();
        match.setParticipant1(participant1);
        match.setParticipant2(participant2);
        match.setResult(MatchResult.WHITE_WIN);

        // Step 4: Создаем раунд и добавляем матч в раунд
        RoundEntity round = new RoundEntity();
        round.setMatches(List.of(match));
        round.setFinished(true);

        // Step 5: Создаем турнир и добавляем раунд
        TournamentEntity tournament = new TournamentEntity();
        tournament.setId(UUID.randomUUID());
        tournament.setName("Test Tournament");
        tournament.setRounds(List.of(round));
        tournament.setStatus(TournamentStatus.ACTIVE);

        // Step 6: Рассчитываем начальный рейтинг ELO
        eloService.processTournamentAndUpdateElo(tournament);

        assertEquals(1210, user1.getEloPoints());
        assertEquals(1190, user2.getEloPoints());

        // Step 7: Изменяем результат матча на ничью (DRAW)
        match.setResult(MatchResult.BLACK_WIN);


        // Step 8: Откатываем изменения рейтинга ELO
        eloService.rollbackEloChanges(tournament);

        // Проверяем, что рейтинги были откатаны
        assertEquals(1200, user1.getEloPoints());
        assertEquals(1200, user2.getEloPoints());

        // Step 9: Пересчитываем ELO с учетом нового результата
        eloService.processTournamentAndUpdateElo(tournament);

        // Step 10: Проверяем, что рейтинги изменились корректно
        assertEquals(1190, user1.getEloPoints());
        assertEquals(1210, user2.getEloPoints());
    }

    @Test
    public void testEloRecalculationAfterNoChange() {
        // Step 1: Создаем двух пользователей
        UserEntity user1 = new UserEntity();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setEloPoints(1200);

        UserEntity user2 = new UserEntity();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setEloPoints(1200);

        // Step 2: Создаем участников
        ParticipantEntity participant1 = new ParticipantEntity();
        participant1.setId(UUID.randomUUID());
        participant1.setUser(user1);


        ParticipantEntity participant2 = new ParticipantEntity();
        participant2.setId(UUID.randomUUID());
        participant2.setUser(user2);


        // Step 3: Создаем матч с результатом WHITE_WIN
        MatchEntity match = new MatchEntity();
        match.setParticipant1(participant1);
        match.setParticipant2(participant2);
        match.setResult(MatchResult.WHITE_WIN);

        // Step 4: Создаем раунд и добавляем матч в раунд
        RoundEntity round = new RoundEntity();
        round.setMatches(List.of(match));
        round.setFinished(true);

        // Step 5: Создаем турнир и добавляем раунд
        TournamentEntity tournament = new TournamentEntity();
        tournament.setId(UUID.randomUUID());
        tournament.setName("Test Tournament");
        tournament.setRounds(List.of(round));
        tournament.setStatus(TournamentStatus.ACTIVE);

        // Step 6: Рассчитываем начальный рейтинг ELO
        eloService.processTournamentAndUpdateElo(tournament);

        assertEquals(1210, user1.getEloPoints());
        assertEquals(1190, user2.getEloPoints());

        // Step 7: Изменяем результат матча на ничью (DRAW)
        match.setResult(MatchResult.WHITE_WIN);


        // Step 8: Откатываем изменения рейтинга ELO
        eloService.rollbackEloChanges(tournament);

        // Проверяем, что рейтинги были откатаны
        assertEquals(1200, user1.getEloPoints());
        assertEquals(1200, user2.getEloPoints());

        // Step 9: Пересчитываем ELO с учетом нового результата
        eloService.processTournamentAndUpdateElo(tournament);

        // Step 10: Проверяем, что рейтинги изменились корректно
        assertEquals(1210, user1.getEloPoints());
        assertEquals(1190, user2.getEloPoints());
    }
    @Test
    public void testAuthorizedVsUnauthorizedUserMatch() {

        UserEntity user1 = new UserEntity();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setEloPoints(1200);

        UserEntity user2 = new UserEntity();
        user2.setId(null);
        user2.setUsername("user2");
        user2.setEloPoints(0);

        ParticipantEntity participant1 = new ParticipantEntity();
        participant1.setId(UUID.randomUUID());
        participant1.setUser(user1);

        ParticipantEntity participant2 = new ParticipantEntity();
        participant2.setId(UUID.randomUUID());
        participant2.setUser(user2);

        boolean isUser1Authorized = SecurityUtil.isAuthorizedUser(user1);
        boolean isUser2Authorized = SecurityUtil.isAuthorizedUser(user2);

        assertTrue(isUser1Authorized);
        assertFalse(isUser2Authorized);


        MatchEntity match = new MatchEntity();
        match.setParticipant1(participant1);
        match.setParticipant2(participant2);
        match.setResult(MatchResult.WHITE_WIN);


        // Создаем раунд и добавляем матч
        RoundEntity round = new RoundEntity();
        round.setMatches(List.of(match));
        round.setFinished(true); // помечаем раунд как завершенный

        // Создаем турнир и добавляем раунд
        TournamentEntity tournament = new TournamentEntity();
        tournament.setId(UUID.randomUUID());
        tournament.setName("Test Tournament");
        tournament.setRounds(List.of(round)); // добавляем раунд в турнир
        tournament.setStatus(TournamentStatus.ACTIVE); // статус турнира "В процессе"

        eloService.processTournamentAndUpdateElo(tournament);

        // Проверяем, что рейтинг ELO авторизованного пользователя был корректно обновлен,
        // а неавторизованный пользователь остался без рейтинга
        assertEquals(1205, user1.getEloPoints());
        assertEquals(0, user2.getEloPoints());
    }

    @Test
    public void testDrawMatch() {
        UserEntity user1 = new UserEntity();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setEloPoints(1200);

        UserEntity user2 = new UserEntity();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setEloPoints(1200);

        ParticipantEntity participant1 = new ParticipantEntity();
        participant1.setId(UUID.randomUUID());
        participant1.setUser(user1);

        ParticipantEntity participant2 = new ParticipantEntity();
        participant2.setId(UUID.randomUUID());
        participant2.setUser(user2);

        // Создаем матч с ничьей
        MatchEntity match = new MatchEntity();
        match.setParticipant1(participant1);
        match.setParticipant2(participant2);
        match.setResult(MatchResult.DRAW);

        // Создаем раунд и добавляем матч
        RoundEntity round = new RoundEntity();
        round.setMatches(List.of(match));
        round.setFinished(true); // помечаем раунд как завершенный

        // Создаем турнир и добавляем раунд
        TournamentEntity tournament = new TournamentEntity();
        tournament.setId(UUID.randomUUID());
        tournament.setName("Test Tournament");
        tournament.setRounds(List.of(round)); // добавляем раунд в турнир
        tournament.setStatus(TournamentStatus.ACTIVE);

        eloService.processTournamentAndUpdateElo(tournament);

        assertEquals(1200, user1.getEloPoints());
        assertEquals(1200, user2.getEloPoints());
    }

    @Test
    public void testGeneralScenarioWithMultipleRounds() {

        UserEntity user1 = new UserEntity();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setEloPoints(1200);

        UserEntity user2 = new UserEntity();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setEloPoints(1200);

        UserEntity user3 = new UserEntity();
        user3.setId(null);
        user3.setUsername("user3");
        user3.setEloPoints(0);

        UserEntity user4 = new UserEntity();
        user4.setId(null);
        user4.setUsername("user4");
        user4.setEloPoints(0);


        ParticipantEntity participant1 = new ParticipantEntity();
        participant1.setId(UUID.randomUUID());
        participant1.setUser(user1);

        ParticipantEntity participant2 = new ParticipantEntity();
        participant2.setId(UUID.randomUUID());
        participant2.setUser(user2);

        ParticipantEntity participant3 = new ParticipantEntity();
        participant3.setId(UUID.randomUUID());
        participant3.setUser(user3);

        ParticipantEntity participant4 = new ParticipantEntity();
        participant4.setId(UUID.randomUUID());
        participant4.setUser(user4);


        boolean isUser1Authorized = SecurityUtil.isAuthorizedUser(user1);
        boolean isUser2Authorized = SecurityUtil.isAuthorizedUser(user2);
        boolean isUser3Authorized = SecurityUtil.isAuthorizedUser(user3);
        boolean isUser4Authorized = SecurityUtil.isAuthorizedUser(user4);

        assertTrue(isUser1Authorized); // user1 авторизован
        assertTrue(isUser2Authorized); // user2 авторизован
        assertFalse(isUser3Authorized); // user3 неавторизован
        assertFalse(isUser4Authorized); // user4 неавторизован

        MatchEntity match1 = new MatchEntity();
        match1.setParticipant1(participant1);
        match1.setParticipant2(participant2);
        match1.setResult(MatchResult.WHITE_WIN);

        MatchEntity match2 = new MatchEntity();
        match2.setParticipant1(participant3);
        match2.setParticipant2(participant4);
        match2.setResult(MatchResult.BLACK_WIN);

        MatchEntity match3 = new MatchEntity();
        match3.setParticipant1(participant1);
        match3.setParticipant2(participant3);
        match3.setResult(MatchResult.WHITE_WIN);

        MatchEntity match4 = new MatchEntity();
        match4.setParticipant1(participant2);
        match4.setParticipant2(participant4);
        match4.setResult(MatchResult.DRAW);

        // Создаем раунды и добавляем матчи
        RoundEntity round1 = new RoundEntity();
        round1.setMatches(List.of(match1, match2));
        round1.setFinished(true);

        RoundEntity round2 = new RoundEntity();
        round2.setMatches(List.of(match3, match4));
        round2.setFinished(true);

        // Создаем турнир и добавляем раунды
        TournamentEntity tournament = new TournamentEntity();
        tournament.setId(UUID.randomUUID());
        tournament.setName("Test Tournament");
        tournament.setRounds(List.of(round1, round2)); // добавляем раунды в турнир
        tournament.setStatus(TournamentStatus.ACTIVE); // статус турнира "В процессе"

        // Обрабатываем турнир и обновляем рейтинг
        eloService.processTournamentAndUpdateElo(tournament);


        assertEquals(1215, user1.getEloPoints());  // user1 выиграл 2 раза: один раз против авторизованного (+10) и один раз против неавторизованного (+5)
        assertEquals(1190, user2.getEloPoints());  // user2 проиграл 1 раз (-10) и сыграл вничью (0)
        assertEquals(0, user3.getEloPoints());     // user3 неавторизован, его рейтинг не должен измениться
        assertEquals(0, user4.getEloPoints());     // user4 неавторизован, его рейтинг не должен измениться
    }

    private static class InMemoryParticipantRepository implements ParticipantRepository {
        private final Map<UUID, ParticipantEntity> data = new HashMap<>();

        @Override
        public <S extends ParticipantEntity> S save(S participant) {
            if (participant.getId() == null) {
                participant.setId(UUID.randomUUID());  // Присваиваем новый UUID, если ID отсутствует
            }
            data.put(participant.getId(), participant);
            return participant;
        }

        @Override
        public <S extends ParticipantEntity> Iterable<S> saveAll(Iterable<S> entities) {
            return null;
        }

        @Override
        public Optional<ParticipantEntity> findById(UUID id) {
            return Optional.ofNullable(data.get(id));  // Возвращаем Optional, чтобы соответствовать CrudRepository
        }

        @Override
        public boolean existsById(UUID uuid) {
            return false;
        }

        public List<ParticipantEntity> findAll() {
            return new ArrayList<>(data.values());
        }

        @Override
        public Iterable<ParticipantEntity> findAllById(Iterable<UUID> uuids) {
            return null;
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void deleteById(UUID uuid) {

        }

        @Override
        public void delete(ParticipantEntity entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends UUID> uuids) {

        }

        @Override
        public void deleteAll(Iterable<? extends ParticipantEntity> entities) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public List<ParticipantEntity> findByTournamentId(UUID tournamentId) {
            return List.of();
        }

        @Override
        public ParticipantEntity findByTournamentIdAndUserId(UUID tournamentId, UUID userId) {
            return null;
        }

        @Override
        public List<ParticipantEntity> findAllByUserId(UUID userId) {
            return List.of();
        }

        @Override
        public Iterable<ParticipantEntity> findAll(Sort sort) {
            return null;
        }

        @Override
        public Page<ParticipantEntity> findAll(Pageable pageable) {
            return null;
        }
    }

    private static class InMemoryTournamentRepository implements TournamentRepository {
        private final Map<UUID, MatchEntity> data = new HashMap<>();



        @Override
        public <S extends TournamentEntity> S save(S entity) {
            return null;
        }

        @Override
        public <S extends TournamentEntity> Iterable<S> saveAll(Iterable<S> entities) {
            return null;
        }

        @Override
        public Optional<TournamentEntity> findById(UUID uuid) {
            return Optional.empty();
        }

        @Override
        public boolean existsById(UUID uuid) {
            return false;
        }

        @Override
        public List<TournamentEntity> findAll() {
            return List.of();
        }

        @Override
        public Iterable<TournamentEntity> findAllById(Iterable<UUID> uuids) {
            return null;
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void deleteById(UUID uuid) {

        }

        @Override
        public void delete(TournamentEntity entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends UUID> uuids) {

        }

        @Override
        public void deleteAll(Iterable<? extends TournamentEntity> entities) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public Iterable<TournamentEntity> findAll(Sort sort) {
            return null;
        }

        @Override
        public Page<TournamentEntity> findAll(Pageable pageable) {
            return null;
        }
    }

    private static class InMemoryUserRepository implements UserRepository {
        private final Map<UUID, UserEntity> data = new HashMap<>();

        @Override
        public <S extends UserEntity> S save(S user) {
            if (user.getId() == null) {
                user.setId(UUID.randomUUID());  // Присваиваем новый UUID, если ID отсутствует
            }
            data.put(user.getId(), user);
            return user;  // Возвращаем сохраненный объект, чтобы соответствовать CrudRepository
        }

        @Override
        public <S extends UserEntity> Iterable<S> saveAll(Iterable<S> entities) {
            return null;
        }

        @Override
        public Optional<UserEntity> findById(UUID id) {
            return Optional.ofNullable(data.get(id));  // Возвращаем Optional, чтобы соответствовать CrudRepository
        }

        @Override
        public boolean existsById(UUID uuid) {
            return false;
        }

        public List<UserEntity> findAll() {
            return new ArrayList<>(data.values());
        }

        @Override
        public Iterable<UserEntity> findAllById(Iterable<UUID> uuids) {
            return null;
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void deleteById(UUID uuid) {

        }

        @Override
        public void delete(UserEntity entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends UUID> uuids) {

        }

        @Override
        public void deleteAll(Iterable<? extends UserEntity> entities) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public UserEntity findByUsername(String userName) {
            return null;
        }

        @Override
        public void addReputation(UUID userId, Integer amount) {

        }

        @Override
        public List<UserEntity> findAllByBadgeId(UUID badgeId) {
            return List.of();
        }

        @Override
        public BigDecimal getGlobalScore(UUID userId, LocalDateTime globalScoreFromDate, LocalDateTime globalScoreToDate) {
            return null;
        }

        @Override
        public Iterable<UserEntity> findAll(Sort sort) {
            return null;
        }

        @Override
        public Page<UserEntity> findAll(Pageable pageable) {
            return null;
        }
    }
}
