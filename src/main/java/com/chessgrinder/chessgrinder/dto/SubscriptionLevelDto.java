package com.chessgrinder.chessgrinder.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubscriptionLevelDto {
    private String name;
    private String description;
}
