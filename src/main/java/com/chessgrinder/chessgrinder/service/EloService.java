package com.chessgrinder.chessgrinder.service;


import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;


import java.util.List;

public interface EloService {
    void updateElo(MatchEntity match);

    void finalizeEloUpdates (List<ParticipantEntity> participants);

}
