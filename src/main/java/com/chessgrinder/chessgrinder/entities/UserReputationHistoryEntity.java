package com.chessgrinder.chessgrinder.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "user_reputation_history_table")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReputationHistoryEntity extends AbstractAuditingEntity {
    @Id
    @UuidGenerator
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "comment", nullable = false)
    private String comment;
}
