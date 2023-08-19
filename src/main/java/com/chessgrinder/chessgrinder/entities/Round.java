package com.chessgrinder.chessgrinder.entities;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Data
@Entity
@Table(name = "Rounds")
@Slf4j
public class Round {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "round_id")
    private UUID nicknameId;

    @ManyToOne
    @JoinColumn(name="tournament_id")
    private Tournament tournament;
}
