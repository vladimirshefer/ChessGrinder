package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.service.RatingsTournamentListenerImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

@Secured(RoleEntity.Roles.ADMIN)
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @Autowired(required = false)
    private RatingsTournamentListenerImpl ratingsTournamentListener;

    /**
     * Stupid approach for locking, but since only admin can do it, then it is ok for now.
     */
    private final AtomicBoolean ratingsLocked = new AtomicBoolean(false);
    @Autowired
    private TournamentRepository tournamentRepository;

    @Secured(RoleEntity.Roles.ADMIN)
    @GetMapping("ratings/reset")
    public void resetRatings() {
        if (ratingsLocked.getAndSet(true)) {
            throw new RuntimeException("Ratings are locked - other operation is running");
        }
        try {
            ratingsTournamentListener.totalReset();
        } finally {
            ratingsLocked.set(false);
        }
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @GetMapping("ratings/recalculate")
    public void recalculateRatings(){
        if (ratingsLocked.getAndSet(true)) {
            throw new RuntimeException("Ratings are locked - other operation is running");
        }
        try {
            tournamentRepository.findAll()
                    .stream()
                    .sorted(Comparator.nullsLast(Comparator.comparing(TournamentEntity::getDate)))
                    .forEach(tournamentEntity -> ratingsTournamentListener.tournamentFinished(tournamentEntity));
        } finally {
            ratingsLocked.set(false);
        }
    }

}
