package com.chessgrinder.chessgrinder.dto;

import lombok.*;

@Data
@Builder
public class BadgeDto {

    private String title;
    private String description;
    private String imageUrl;
}
