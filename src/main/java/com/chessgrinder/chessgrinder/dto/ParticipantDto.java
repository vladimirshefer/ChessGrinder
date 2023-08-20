package com.chessgrinder.chessgrinder.dto;

import java.math.*;

import lombok.*;

@Data
@Builder
public class ParticipantDto {

    private MemberDto user;
    private String nickname;
    private BigDecimal score;
    private BigDecimal buchholz;
}
