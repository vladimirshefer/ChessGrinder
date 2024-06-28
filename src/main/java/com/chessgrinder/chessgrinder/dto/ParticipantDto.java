package com.chessgrinder.chessgrinder.dto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
public class ParticipantDto {

    private String id;

    @Nullable
    private String userId;

    /**
     * Null if userId is null.
     * But could be null even if userId is not null.
     */
    @Nullable
    String userFullName;

    @Nonnull
    private String name; //NICKNAME

    @Nonnull
    @Builder.Default
    private BigDecimal score = BigDecimal.ZERO;

    @Nonnull
    @Builder.Default
    private BigDecimal buchholz = BigDecimal.ZERO;

    /*
     Boolean (boxed) to force lombok to generate getIsMissing()
     instead of isMissing() getter.
     This is required to make Jackson correctly serialize DTO with
     isMissing field instead of missing field.
    */
    @Builder.Default
    private Boolean isMissing = false;

    @Nonnull
    @Builder.Default
    private Integer place = -1;
}
