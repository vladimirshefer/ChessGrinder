package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.MatchResult;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private MatchRepository matchRepository;
    @Autowired
    private RoundRepository roundRepository;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testStatsVsUser() {
        UserEntity comparableUser = userRepository.save(createUser("comparable"));
        UserEntity opponentUser = userRepository.save(createUser("opponent"));

        // First tournament setup
        TournamentEntity tournamentEntity1 = tournamentRepository.save(createTournament());
        ParticipantEntity participant1Tournament1 = participantRepository.save(createParticipant(comparableUser, tournamentEntity1));
        ParticipantEntity participant2Tournament1 = participantRepository.save(createParticipant(opponentUser, tournamentEntity1));
        RoundEntity roundEntity1Tournament1 = roundRepository.save(createRound(tournamentEntity1));
        matchRepository.save(createMatch(participant1Tournament1, participant2Tournament1, roundEntity1Tournament1, MatchResult.WHITE_WIN));
        RoundEntity roundEntity2Tournament1 = roundRepository.save(createRound(tournamentEntity1));
        matchRepository.save(createMatch(participant1Tournament1, participant2Tournament1, roundEntity2Tournament1, MatchResult.DRAW));
        RoundEntity roundEntity3Tournament1 = roundRepository.save(createRound(tournamentEntity1));
        matchRepository.save(createMatch(participant1Tournament1, participant2Tournament1, roundEntity3Tournament1, MatchResult.BLACK_WIN));

        // Second tournament setup
        TournamentEntity tournamentEntity2 = tournamentRepository.save(createTournament());
        ParticipantEntity participant1Tournament2 = participantRepository.save(createParticipant(comparableUser, tournamentEntity2));
        ParticipantEntity participant2Tournament2 = participantRepository.save(createParticipant(opponentUser, tournamentEntity2));
        RoundEntity roundEntity1Tournament2 = roundRepository.save(createRound(tournamentEntity2));
        matchRepository.save(createMatch(participant1Tournament2, participant2Tournament2, roundEntity1Tournament2, MatchResult.WHITE_WIN));
        RoundEntity roundEntity2Tournament2 = roundRepository.save(createRound(tournamentEntity2));
        matchRepository.save(createMatch(participant2Tournament2, participant1Tournament2, roundEntity2Tournament2, MatchResult.BLACK_WIN));
        RoundEntity roundEntity3Tournament2 = roundRepository.save(createRound(tournamentEntity2));
        matchRepository.save(createMatch(participant1Tournament2, participant2Tournament2, roundEntity3Tournament2, MatchResult.DRAW));

        // When: Invoke the repository method
        List<Integer[]> stats = userRepository.getStatsAgainstUser(comparableUser.getId(), opponentUser.getId());

        // Then: Verify the stats result
        assertThat(stats).isNotNull();
        assertThat(stats.size()).isGreaterThan(0);
        assertThat(stats.get(0)).containsExactly(3, 1, 2);
    }

    @Test
    void testAddReputationCannotBeNegative() {
        UserEntity user = userRepository.save(createUser("test"));
        user.setReputation(10);
        userRepository.save(user);

        {
            userRepository.addReputation(user.getId(), -10);
            UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
            assertThat(updatedUser.getReputation()).isEqualTo(0);
        }

        { // cannot set less than 0
            userRepository.addReputation(user.getId(), -10);
            UserEntity updatedUser = userRepository.findById(user.getId()).orElseThrow();
            assertThat(updatedUser.getReputation()).isEqualTo(0);
        }

    }

    @Test
    void searchAllOrdered_shouldMatchUsernameUsertagAndName() {
        UserEntity usernameUser = userRepository.save(createUser("john@example.com", "alpha", "Somebody", 1200, 10));
        UserEntity usertagUser = userRepository.save(createUser("tag@example.com", "johnTag", "Else", 1100, 9));
        UserEntity nameUser = userRepository.save(createUser("name@example.com", "beta", "John Name", 1000, 8));
        userRepository.save(createUser("other@example.com", "gamma", "Other", 900, 7));

        List<UserEntity> result = userRepository.searchAllOrdered("john", Pageable.ofSize(20).withPage(0)).getContent();

        assertThat(result).extracting(UserEntity::getId)
                .containsExactly(usernameUser.getId(), usertagUser.getId(), nameUser.getId());
    }

    @Test
    void searchAllOrderedPublic_shouldNotMatchUsernameOnly() {
        UserEntity usernameOnly = userRepository.save(createUser("hidden-john@example.com", "alpha", "Somebody", 1200, 10));
        UserEntity usertagUser = userRepository.save(createUser("tag@example.com", "johnTag", "Else", 1100, 9));
        UserEntity nameUser = userRepository.save(createUser("name@example.com", "beta", "John Name", 1000, 8));

        List<UserEntity> result = userRepository.searchAllOrderedPublic("john", Pageable.ofSize(20).withPage(0)).getContent();

        assertThat(result).extracting(UserEntity::getId)
                .containsExactly(usertagUser.getId(), nameUser.getId());
        assertThat(result).extracting(UserEntity::getId).doesNotContain(usernameOnly.getId());
    }

    @Test
    void findAllOrderedByCity_shouldReturnOnlyUsersFromCityOrderedByRating() {
        UserEntity topBerlin = userRepository.save(createUser("top@example.com", "top", "Top Berlin", 2200, 10));
        UserEntity secondBerlin = userRepository.save(createUser("second@example.com", "second", "Second Berlin", 1800, 50));
        UserEntity limassolUser = userRepository.save(createUser("limassol@example.com", "lim", "Limassol User", 2500, 100));

        TournamentEntity berlinTournament = tournamentRepository.save(createTournament("Berlin"));
        TournamentEntity limassolTournament = tournamentRepository.save(createTournament("Limassol"));

        participantRepository.save(createParticipant(topBerlin, berlinTournament));
        participantRepository.save(createParticipant(secondBerlin, berlinTournament));
        participantRepository.save(createParticipant(limassolUser, limassolTournament));

        List<UserEntity> result = userRepository.findAllOrderedByCity("Berlin", Pageable.ofSize(20).withPage(0)).getContent();

        assertThat(result).extracting(UserEntity::getId)
                .containsExactly(topBerlin.getId(), secondBerlin.getId());
    }

    @Test
    void findAllByCityOrderedByReputation_shouldReturnOnlyUsersFromCityOrderedByReputation() {
        UserEntity topBerlin = userRepository.save(createUser("top@example.com", "top", "Top Berlin", 1800, 200));
        UserEntity secondBerlin = userRepository.save(createUser("second@example.com", "second", "Second Berlin", 2400, 100));
        UserEntity limassolUser = userRepository.save(createUser("limassol@example.com", "lim", "Limassol User", 2500, 500));

        TournamentEntity berlinTournament = tournamentRepository.save(createTournament("Berlin"));
        TournamentEntity limassolTournament = tournamentRepository.save(createTournament("Limassol"));

        participantRepository.save(createParticipant(topBerlin, berlinTournament));
        participantRepository.save(createParticipant(secondBerlin, berlinTournament));
        participantRepository.save(createParticipant(limassolUser, limassolTournament));

        List<UserEntity> result = userRepository.findAllByCityOrderedByReputation("Berlin", Pageable.ofSize(20).withPage(0)).getContent();

        assertThat(result).extracting(UserEntity::getId)
                .containsExactly(topBerlin.getId(), secondBerlin.getId());
    }

    @Test
    void findAllOrderedByReputation_shouldOrderGloballyByReputation() {
        UserEntity first = userRepository.save(createUser("first@example.com", "first", "First", 1500, 300));
        UserEntity second = userRepository.save(createUser("second@example.com", "second", "Second", 2200, 200));
        UserEntity third = userRepository.save(createUser("third@example.com", "third", "Third", 2500, 100));

        List<UserEntity> result = userRepository.findAllOrderedByReputation(Pageable.ofSize(20).withPage(0)).getContent();

        assertThat(result).extracting(UserEntity::getId)
                .containsExactly(first.getId(), second.getId(), third.getId());
    }

    @Test
    void activeCityBackfillMigration_shouldUseCityOfLatestFinishedTournament() throws Exception {
        UserEntity player = userRepository.save(createUser("player@example.com", "player", "Player", 1500, 0));
        UserEntity noFinished = userRepository.save(createUser("nofinished@example.com", "nofin", "No Finished", 1500, 0));
        UserEntity noTournaments = userRepository.save(createUser("nobody@example.com", "nobody", "Nobody", 1500, 0));

        LocalDateTime now = LocalDateTime.now();
        TournamentEntity oldBerlin = tournamentRepository.save(createTournament("Berlin", TournamentStatus.FINISHED, now.minusDays(30)));
        TournamentEntity latestTbilisi = tournamentRepository.save(createTournament("Tbilisi", TournamentStatus.FINISHED, now.minusDays(10)));
        TournamentEntity latestNoCity = tournamentRepository.save(createTournament(null, TournamentStatus.FINISHED, now.minusDays(5)));
        TournamentEntity plannedLimassol = tournamentRepository.save(createTournament("Limassol", TournamentStatus.PLANNED, now.plusDays(5)));
        participantRepository.save(createParticipant(player, oldBerlin));
        participantRepository.save(createParticipant(player, latestTbilisi));
        participantRepository.save(createParticipant(player, latestNoCity));
        participantRepository.save(createParticipant(player, plannedLimassol));
        participantRepository.save(createParticipant(noFinished, plannedLimassol));
        entityManager.flush();

        String migration = new String(new ClassPathResource("db/migration/V202610251800__alter_table__users_table__add_active_city.sql")
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String update = migration.substring(migration.indexOf("UPDATE users_table"));
        entityManager.getEntityManager().createNativeQuery(update.replace(";", "")).executeUpdate();
        entityManager.clear();

        assertThat(userRepository.findById(player.getId()).orElseThrow().getActiveCity()).isEqualTo("Tbilisi");
        assertThat(userRepository.findById(noFinished.getId()).orElseThrow().getActiveCity()).isNull();
        assertThat(userRepository.findById(noTournaments.getId()).orElseThrow().getActiveCity()).isNull();
    }

    private static TournamentEntity createTournament(String city, TournamentStatus status, LocalDateTime date) {
        return TournamentEntity.builder()
                .status(status)
                .date(date)
                .roundsNumber(6)
                .city(city)
                .build();
    }

    private static MatchEntity createMatch(ParticipantEntity comparableUserParticipant, ParticipantEntity opponentUserParticipant, RoundEntity roundEntity, MatchResult matchResult) {
        return MatchEntity.builder()
                .participant1(comparableUserParticipant)
                .participant2(opponentUserParticipant)
                .result(matchResult)
                .round(roundEntity)
                .build();
    }

    private static RoundEntity createRound(TournamentEntity tournamentEntity) {
        return RoundEntity.builder()
                .tournament(tournamentEntity)
                .isFinished(true)
                .build();
    }

    private static TournamentEntity createTournament() {
        return TournamentEntity.builder()
                .status(TournamentStatus.FINISHED)
                .date(LocalDateTime.now())
                .roundsNumber(6)
                .build();
    }

    private static TournamentEntity createTournament(String city) {
        return TournamentEntity.builder()
                .status(TournamentStatus.FINISHED)
                .date(LocalDateTime.now())
                .roundsNumber(6)
                .city(city)
                .build();
    }

    private static ParticipantEntity createParticipant(
            UserEntity comparableUser,
            TournamentEntity tournament
    ) {
        return ParticipantEntity.builder()
                .user(comparableUser)
                .nickname(comparableUser.getName())
                .tournament(tournament)
                .buchholz(BigDecimal.ZERO)
                .score(BigDecimal.ZERO)
                .place(1)
                .build();
    }

    private static UserEntity createUser(String username) {
        return UserEntity.builder()
                .name(username)
                .build();
    }

    private static UserEntity createUser(String username,
                                         String usertag,
                                         String fullName,
                                         int eloPoints,
                                         int reputation) {
        return UserEntity.builder()
                .name(fullName)
                .username(username)
                .usertag(usertag)
                .eloPoints(eloPoints)
                .reputation(reputation)
                .build();
    }

    @TestConfiguration
    static class AuditorAwareTestConfig {
        @Bean
        public AuditorAware<String> auditorProvider() {
            return () -> Optional.of("test_user");
        }
    }
}
