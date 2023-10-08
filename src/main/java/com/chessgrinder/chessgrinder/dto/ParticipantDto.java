package com.chessgrinder.chessgrinder.dto;

import java.math.*;

import jakarta.annotation.Nullable;
import lombok.*;

@Data
@Builder
public class ParticipantDto {

    @Nullable
    private String id;
    @Nullable
    private String userId;
    private TournamentDto tournament;
    @NonNull
    private String name; //NICKNAME
    @NonNull
    private BigDecimal score = BigDecimal.ZERO;
    @NonNull
    private BigDecimal buchholz = BigDecimal.ZERO;

}
