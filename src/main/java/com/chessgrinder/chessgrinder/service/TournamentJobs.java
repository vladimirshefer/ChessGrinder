package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TournamentJobs {

    private final TournamentRepository tournamentRepository;
    private final TournamentService tournamentService;

    /**
     * This method is scheduled and tries to close the tournament if it is abandoned or stale.
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanup() {
        List<TournamentEntity> tournaments = tournamentRepository.findAllByStatus(TournamentStatus.ACTIVE);
        for (TournamentEntity tournament : tournaments) {

            if (tournament.getStatus().equals(TournamentStatus.ACTIVE)) {
                deleteTournamentIfAbandoned(tournament);
                closeTournamentIfStale(tournament);
            }
        }

        List<TournamentEntity> plannedTournaments = tournamentRepository.findAllByStatus(TournamentStatus.PLANNED);
        for (TournamentEntity tournament : plannedTournaments) {
            deleteTournamentIfAbandoned(tournament);
        }
    }

    private void closeTournamentIfStale(TournamentEntity tournament) {
        LocalDateTime date = tournament.getDate();
        if (date.isBefore(LocalDateTime.now().minusDays(1))) {
            try {
                tournamentService.finishTournament(tournament.getId());
            } catch (Exception e) {
                log.error("Could not finish tournament with ID {} during cleanup", tournament.getId(), e);
            }
        }
    }

    private void deleteTournamentIfAbandoned(TournamentEntity tournament) {
        LocalDateTime date = tournament.getDate();
        if (date.isBefore(LocalDateTime.now().minusDays(1)) && tournament.getRounds().isEmpty()) {
            try {
                tournamentService.deleteTournament(tournament.getId());
                log.info("Deleted abandoned tournament with ID {} during cleanup", tournament.getId());
            } catch (Exception e) {
                log.error("Could not delete tournament with ID {} during cleanup", tournament.getId(), e);
            }
        }
    }
}
