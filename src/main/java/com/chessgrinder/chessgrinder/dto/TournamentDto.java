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
    public static final String API_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm";

    private String id;

    @Nullable
    private String name;
    @Nullable
    private String locationName;
    @Nullable
    private String locationUrl;
    @Nullable
    private String pairingStrategy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = API_DATETIME_FORMAT)
    private LocalDateTime date;

    private TournamentStatus status;

    private Integer roundsNumber;
}
