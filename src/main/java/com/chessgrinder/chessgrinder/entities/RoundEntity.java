package com.chessgrinder.chessgrinder.entities;

import java.util.*;

import com.chessgrinder.chessgrinder.enums.MatchResult;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.*;
import lombok.extern.slf4j.*;
import org.hibernate.annotations.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "rounds_table")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoundEntity extends AbstractAuditingEntity {

    @Id
    @Column(name = "id")
    @UuidGenerator
    private UUID id;

    @Column(name = "number")
    private Integer number;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private TournamentEntity tournament;

    @ToString.Exclude
    @OneToMany(mappedBy = "round", cascade = CascadeType.REMOVE)
    private List<MatchEntity> matches;

    @Column(name = "is_finished")
    private boolean isFinished;

    public void addMatch(MatchEntity match) {
        if (matches == null) {
            matches = new ArrayList<>();
        }
        matches.add(match);
    }
}
