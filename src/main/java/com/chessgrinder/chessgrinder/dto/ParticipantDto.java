package com.chessgrinder.chessgrinder.dto;

import java.math.*;

import lombok.*;

@Data
@Builder
public class ParticipantDto {

    private UserDto user;
    private String nickname;
    private BigDecimal score;
    private BigDecimal buchholz;
}