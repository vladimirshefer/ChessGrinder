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

@Getter
@Setter
@ToString
@Entity
@Table(name = "tournaments_table")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentEntity extends AbstractAuditingEntity {

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

    @Nullable
    @Column(name = "city")
    private String city;

    @Column(name = "date")
    private LocalDateTime date;

    // For new two-pairing system
    @Nullable
    @Column(name = "pairing_strategy")
    private String pairingStrategy;

    @Column(name = "status")
    @Enumerated(EnumType. STRING)
    private TournamentStatus status;

    @Nullable
    @Column(name = "has_elo_calculated")
    private boolean hasEloCalculated;

    /**
     * Number of allowed rounds (not actual number of rounds)
     */
    @Column(name = "rounds_number")
    private Integer roundsNumber;

    @Nullable
    @Column(name = "registration_limit")
    private Integer registrationLimit;

    @Nullable
    @Column(name = "repeatable")
    @Enumerated(EnumType.STRING)
    private RepeatableType repeatable;

    @ToString.Exclude
    @OneToMany(mappedBy = "tournament")
    private List<RoundEntity> rounds;

    @Nullable
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

}
