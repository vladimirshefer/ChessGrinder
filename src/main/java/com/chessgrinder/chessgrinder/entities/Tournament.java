package com.chessgrinder.chessgrinder.entities;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Data
@Entity
@Table(name = "Tournaments")
@Slf4j
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "tournament_id")
    private UUID tournamentId;

    @ManyToMany(mappedBy = "tournaments")
    List<User> players;

    @OneToMany(mappedBy = "tournament")
    private List<Round> rounds;



}
