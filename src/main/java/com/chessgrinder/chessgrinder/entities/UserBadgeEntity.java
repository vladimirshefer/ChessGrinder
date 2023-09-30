package com.chessgrinder.chessgrinder.entities;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "users_badges_table")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBadgeEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name="badge_id")
    private BadgeEntity badge;
}
