package com.chessgrinder.chessgrinder.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserHistoryRecordDto {
    private TournamentDto tournament;
    private ParticipantDto participant;
}
