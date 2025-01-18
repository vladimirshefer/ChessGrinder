package com.chessgrinder.chessgrinder.dto;

import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TournamentDto {

    private String id;

    @Nullable
    private String name;
    @Nullable
    private String locationName;
    @Nullable
    private String locationUrl;
    @Nullable
    private String pairingStrategy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime date;

    private TournamentStatus status;

    private Integer roundsNumber;

    @Nullable
    private ClubDto clubDto;
}
