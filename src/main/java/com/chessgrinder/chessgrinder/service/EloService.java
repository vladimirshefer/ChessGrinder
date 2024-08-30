package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.MatchEntity;



import java.util.List;

public interface EloService {

    void finalizeEloUpdates(List<MatchEntity> matches);
}
