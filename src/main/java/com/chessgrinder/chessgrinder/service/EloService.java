package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.TournamentEntity;


public interface EloService {

    void processTournamentAndUpdateElo(TournamentEntity tournament);
    void rollbackEloChanges(TournamentEntity tournament);
}
