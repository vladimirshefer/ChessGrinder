package com.chessgrinder.chessgrinder.entities;

import java.math.*;
import java.util.*;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "rounds")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Round {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "number")
    private Integer number;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ToString.Exclude
    @OneToMany(mappedBy = "round", cascade = CascadeType.REMOVE)
    private List<Match> matches;

    @Column(name = "is_finished")
    private boolean isFinished;
}
