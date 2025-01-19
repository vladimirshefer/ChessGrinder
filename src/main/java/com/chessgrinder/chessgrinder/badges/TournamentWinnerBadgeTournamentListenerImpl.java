package com.chessgrinder.chessgrinder.badges;

import com.chessgrinder.chessgrinder.entities.BadgeEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.entities.UserBadgeEntity;
import com.chessgrinder.chessgrinder.repositories.BadgeRepository;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.UserBadgeRepository;
import com.chessgrinder.chessgrinder.service.TournamentService.TournamentListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TournamentWinnerBadgeTournamentListenerImpl implements TournamentListener {

    private static final String BADGE_TITLE = "Tournament Winner";

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final ParticipantRepository participantRepository;

    @Override
    public void tournamentFinished(TournamentEntity tournamentEntity) {
        ParticipantEntity winner = participantRepository.findFirstPlaceByTournamentId(tournamentEntity.getId()).orElse(null);

        if (winner == null || winner.getUser() == null) {
            return;
        }

        BadgeEntity badge = getOrCreateBadge();
        userBadgeRepository.save(
                UserBadgeEntity.builder()
                        .user(winner.getUser())
                        .badge(badge)
                        .build()
        );
    }

    @Override
    public void tournamentReopened(TournamentEntity tournamentEntity) {
        // Do nothing
    }

    @Override
    public void totalReset() {
        BadgeEntity badge = getOrCreateBadge();
        userBadgeRepository.deleteAllByBadgeId(badge.getId());
    }

    private BadgeEntity getOrCreateBadge() {
        BadgeEntity badge = badgeRepository.findByTitle(BADGE_TITLE).orElse(null);
        if (badge == null) {
            badge = badgeRepository.save(BadgeEntity.builder()
                    .title(BADGE_TITLE)
                    .description("Badge given to the winner of a tournament.")
                    .build()
            );
        }
        return badge;
    }

}
