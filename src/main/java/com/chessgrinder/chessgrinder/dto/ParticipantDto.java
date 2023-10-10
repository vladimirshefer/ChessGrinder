package com.chessgrinder.chessgrinder.dto;

import java.math.*;

import jakarta.annotation.Nullable;
import lombok.*;

@Data
@Builder
public class ParticipantDto {

    private String id;
    @Nullable
    private String userId;
    @NonNull
    private String name; //NICKNAME
    @NonNull
    private BigDecimal score = BigDecimal.ZERO;
    @NonNull
    private BigDecimal buchholz = BigDecimal.ZERO;

}
