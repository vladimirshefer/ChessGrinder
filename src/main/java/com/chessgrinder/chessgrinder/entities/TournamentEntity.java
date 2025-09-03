package com.chessgrinder.chessgrinder.entities;

import com.chessgrinder.chessgrinder.enums.RepeatableType;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    @Builder.Default
    private List<RoundEntity> rounds = new ArrayList<>();

    @Nullable
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

}
