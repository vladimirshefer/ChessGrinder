package com.chessgrinder.chessgrinder.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(name = "subscriptions_table")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEntity extends AbstractAuditingEntity {

    @Id
    @Column(name = "id")
    @UuidGenerator
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private ClubEntity club;

    @ManyToOne
    @JoinColumn(name = "subscription_level_id")
    private SubscriptionLevelEntity subscriptionLevel;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "finish_date")
    private Instant finishDate;
}
