package com.chessgrinder.chessgrinder.entities;

import java.time.*;
import java.util.*;

import com.chessgrinder.chessgrinder.enums.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "tournaments_table")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TournamentEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "status")
    @Enumerated(EnumType. STRING)
    private TournamentStatus status;

    @ToString.Exclude
    @OneToMany(mappedBy = "tournamentEntity")
    private List<RoundEntity> roundEntities;
}
