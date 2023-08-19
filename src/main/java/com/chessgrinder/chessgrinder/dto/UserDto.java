package com.chessgrinder.chessgrinder.dto;

import java.util.*;

import lombok.*;

@Data
public class UserDto {

    private String name;
    private List<BadgeDto> userBadges;
}
