package com.chessgrinder.chessgrinder.entities;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user_badges")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBadge {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="badge_id")
    private Badge badge;
}
