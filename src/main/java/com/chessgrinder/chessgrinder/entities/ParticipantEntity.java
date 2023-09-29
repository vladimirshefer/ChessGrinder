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
@Table(name = "participants_table")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private TournamentEntity tournament;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "score")
    private BigDecimal score;

    @Column(name = "buchholz")
    private BigDecimal buchholz;

    @Column(name = "is_missing")
    private boolean isMissing;

}
