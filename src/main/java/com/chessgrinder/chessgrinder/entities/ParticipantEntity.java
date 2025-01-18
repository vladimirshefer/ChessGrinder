package com.chessgrinder.chessgrinder.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(name = "participants_table")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantEntity extends AbstractAuditingEntity {

    @Id
    @Column(name = "id")
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Nullable
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "tournament_id")
    @Nullable // Tournament could be deleted from db.
    private TournamentEntity tournament;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "initial_elo_points")
    private int initialEloPoints;

    /**
     * Misleading name: Actually it is Elo Diff
     */
    @Column (name ="final_elo_points")
    private int finalEloPoints;

    @Column(name = "score", nullable = false)
    private BigDecimal score;

    @Column(name = "buchholz", nullable = false)
    private BigDecimal buchholz;

    @Column(name = "is_missing")
    private boolean isMissing;

    @Column(name = "is_moderator")
    private boolean isModerator;

    @Column(name = "place", nullable = false)
    private Integer place;
}
