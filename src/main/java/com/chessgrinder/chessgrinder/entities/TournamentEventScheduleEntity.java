package com.chessgrinder.chessgrinder.entities;

import com.chessgrinder.chessgrinder.enums.TournamentEventScheduleStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Represents a schedule for tournament events that repeat on a weekly basis.
 * Each schedule has a name, a day of week, a time, and a list of tournament events.
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "tournament_event_schedules_table")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentEventScheduleEntity extends AbstractAuditingEntity {

    @Id
    @Column(name = "id")
    @UuidGenerator
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    /**
     * The day of the week when the tournament event is scheduled.
     */
    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    /**
     * The time of day when the tournament event is scheduled.
     */
    @Column(name = "time")
    private LocalTime time;

    /**
     * The status of the schedule (ACTIVE, PAUSED, ARCHIVED).
     */
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentEventScheduleStatus status;

    /**
     * The tournament events that are part of this schedule.
     */
    @ToString.Exclude
    @OneToMany(mappedBy = "schedule", cascade = jakarta.persistence.CascadeType.ALL)
    private List<TournamentEventEntity> events;
}
