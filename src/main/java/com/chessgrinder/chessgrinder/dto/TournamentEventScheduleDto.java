package com.chessgrinder.chessgrinder.dto;

import com.chessgrinder.chessgrinder.enums.TournamentEventScheduleStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class TournamentEventScheduleDto {
    public static final String API_TIME_FORMAT = "HH:mm";

    private String id;

    private String name;
    
    /**
     * The day of the week when the tournament event is scheduled.
     */
    private DayOfWeek dayOfWeek;

    /**
     * The time of day when the tournament event is scheduled.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = API_TIME_FORMAT)
    private LocalTime time;

    /**
     * The status of the schedule (ACTIVE, PAUSED, ARCHIVED).
     */
    private TournamentEventScheduleStatus status;

    /**
     * The tournament events that are part of this schedule.
     */
    @Nullable
    private List<TournamentEventDto> events;
}