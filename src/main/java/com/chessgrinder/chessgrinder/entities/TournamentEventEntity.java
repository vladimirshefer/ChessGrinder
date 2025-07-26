package com.chessgrinder.chessgrinder.entities;

import java.time.*;
import java.util.*;

import com.chessgrinder.chessgrinder.enums.*;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import lombok.extern.slf4j.*;
import org.hibernate.annotations.*;

/**
 * Represents a tournament event that can contain multiple tournaments (brackets).
 * Users register for the event, and the system distributes them across tournaments
 * based on their ratings.
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "tournament_events_table")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentEventEntity extends AbstractAuditingEntity {

    @Id
    @Column(name = "id")
    @UuidGenerator
    private UUID id;

    @Nullable
    @Column(name = "name")
    private String name;

    @Nullable
    @Column(name = "location_name")
    private String locationName;

    @Nullable
    @Column(name = "location_url")
    private String locationUrl;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private TournamentStatus status;

    /**
     * Number of planned rounds (not actual number of rounds)
     */
    @Column(name = "rounds_number")
    private Integer roundsNumber;

    @Nullable
    @Column(name = "registration_limit")
    private Integer registrationLimit;

    /**
     * The schedule this event belongs to (optional).
     */
    @ManyToOne
    @JoinColumn(name = "schedule_id")
    @Nullable
    private TournamentEventScheduleEntity schedule;

    /**
     * The tournaments (brackets) that are part of this event.
     */
    @ToString.Exclude
    @OneToMany(mappedBy = "event", cascade = jakarta.persistence.CascadeType.ALL)
    private List<TournamentEntity> tournaments;

    /**
     * The participants registered for this event.
     * They will be distributed across tournaments when the event begins.
     */
    @ToString.Exclude
    @OneToMany(mappedBy = "event", cascade = jakarta.persistence.CascadeType.ALL)
    private List<ParticipantEntity> participants;
}
