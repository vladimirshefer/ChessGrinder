package com.chessgrinder.chessgrinder.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClubDto {
    private String id;
    private String name;
    private String description;
    private String location;
}
