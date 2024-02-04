package com.chessgrinder.chessgrinder.chessengine;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;

public interface MatchupStrategy {

    List<MatchDto> matchUp(
            List<ParticipantDto> participantIds,
            /**
             * List per round.
             */
            List<List<MatchDto>> matchHistory,
            boolean recalculateResults
    );
}


