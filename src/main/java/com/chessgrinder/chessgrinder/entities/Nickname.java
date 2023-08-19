package com.chessgrinder.chessgrinder.entities;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Data
@Entity
@Table(name = "Nicknames")
@Slf4j
public class Nickname {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "nickname_id")
    private UUID nicknameId;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
