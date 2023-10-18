package com.chessgrinder.chessgrinder.entities;

import com.chessgrinder.chessgrinder.enums.MatchResult;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

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
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "round_id")
    private RoundEntity round;

    @ManyToOne
    @JoinColumn(name = "participant_id_1")
    @Nullable
    private ParticipantEntity participant1;

    @ManyToOne
    @JoinColumn(name = "participant_id_2")
    @Nullable
    private ParticipantEntity participant2;

    @Column(name = "result")
    @Enumerated(EnumType. STRING)
    @Nullable
    private MatchResult result;

}
