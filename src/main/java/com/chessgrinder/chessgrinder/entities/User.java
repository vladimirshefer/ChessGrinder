package com.chessgrinder.chessgrinder.entities;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.*;



@Data
@Entity
@Table(name = "Users")
@Slf4j
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(name = "user_name", unique = true)
    private String userName;

    @Column(name = "user_surname", unique = true)
    private String userSurname;

    @OneToMany(mappedBy = "user")
    private List<Nickname> userNicknames;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "User_Tournaments",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "tournament_id") }
    )
    private List<Tournament> tournaments;
}
