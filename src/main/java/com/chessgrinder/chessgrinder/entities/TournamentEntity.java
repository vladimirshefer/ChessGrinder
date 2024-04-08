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

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "status")
    @Enumerated(EnumType. STRING)
    private TournamentStatus status;

    /**
     * Number of allowed rounds (not actual number of rounds)
     */
    @Column(name = "number_of_rounds")
    private Integer numberOfRounds;

    @ToString.Exclude
    @OneToMany(mappedBy = "tournament")
    private List<RoundEntity> rounds;
}
