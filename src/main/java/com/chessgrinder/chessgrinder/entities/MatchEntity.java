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
@Table(name = "matches_table")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "round_id")
    private RoundEntity round;

    @ManyToOne
    @JoinColumn(name = "player_id_1")
    private ParticipantEntity participant1;

    @ManyToOne
    @JoinColumn(name = "player_id_2")
    private ParticipantEntity participant2;

    @Column(name = "result")
    @Enumerated(EnumType. STRING)
    private MatchResult result;

}
