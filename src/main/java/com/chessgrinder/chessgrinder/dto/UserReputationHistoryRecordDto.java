package com.chessgrinder.chessgrinder.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserReputationHistoryRecordDto {
    private String userId;
    private Integer amount;
    private String comment;
}
