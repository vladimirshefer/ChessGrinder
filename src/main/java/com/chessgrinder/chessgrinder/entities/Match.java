package com.chessgrinder.chessgrinder.entities;

import java.util.*;

import com.chessgrinder.chessgrinder.enums.*;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "matches")
@Slf4j
public class Match {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "round_id")
    private Round round;

    @ManyToOne
    @JoinColumn(name = "player_id_1")
    private User user1;

    @ManyToOne
    @JoinColumn(name = "player_id_2")
    private User user2;

    @Column(name = "result")
    @Enumerated(EnumType. STRING)
    private MatchResult result;

}
