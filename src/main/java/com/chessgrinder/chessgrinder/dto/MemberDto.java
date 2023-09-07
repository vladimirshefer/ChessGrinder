package com.chessgrinder.chessgrinder.dto;

import java.util.*;

import lombok.*;

@Data
@Builder
public class MemberDto {
    private String id;
    private String name;
    private List<BadgeDto> badges;
}
