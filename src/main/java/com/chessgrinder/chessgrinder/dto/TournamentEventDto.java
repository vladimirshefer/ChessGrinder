package com.chessgrinder.chessgrinder.dto;

import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TournamentEventDto {
    public static final String API_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm";

    private String id;

    @Nullable
    private String name;

    @Nullable
    private String locationName;

    @Nullable
    private String locationUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = API_DATETIME_FORMAT)
    private LocalDateTime date;

    private TournamentStatus status;

    private Integer roundsNumber;

    @Nullable
    private Integer registrationLimit;

    @Nullable
    private List<TournamentDto> tournaments;

    @Nullable
    private List<ParticipantDto> participants;
}