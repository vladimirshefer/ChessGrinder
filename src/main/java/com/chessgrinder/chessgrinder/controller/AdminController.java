package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.dto.ListDto;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.service.TournamentService.TournamentListener;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Secured(RoleEntity.Roles.ADMIN)
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @Autowired(required = false)
    private List<TournamentListener> tournamentListeners;

    /**
     * Stupid approach for locking, but since only admin can do it, then it is ok for now.
     */
    private final AtomicBoolean lock = new AtomicBoolean(false);
    @Autowired
    private TournamentRepository tournamentRepository;

    @Secured(RoleEntity.Roles.ADMIN)
    @GetMapping("tournament-listener/{name}/reset")
    public void resetListener(
            @PathVariable String name
    ) {
        withLock(() -> getListener(name).totalReset());
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @GetMapping("tournament-listener/{name}/recalculate")
    public void recalculateListener(
            @PathVariable String name
    ) {
        withLock(() -> tournamentRepository.findAll()
                .stream()
                .sorted(Comparator.nullsLast(Comparator.comparing(TournamentEntity::getDate)))
                .forEach(tournamentEntity -> getListener(name).tournamentFinished(tournamentEntity))
        );
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @GetMapping("tournament-listener")
    public ListDto<String> getListeners() {
        return ListDto.of(tournamentListeners.stream().map(it -> it.getClass().getSimpleName()).toList());
    }

    private TournamentListener getListener(String name) {
        if (tournamentListeners == null) {
            throw new RuntimeException("No tournament listeners found");
        }
        return tournamentListeners.stream()
                .filter(listener -> listener.getClass().getSimpleName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No tournament listener found with name " + name));
    }

    private void withLock(Runnable action) {
        if (lock.compareAndExchange(false, true)) {
            throw new RuntimeException("Locked - other operation is running");
        }
        try {
            action.run();
        } finally {
            lock.set(false);
        }
    }


}
