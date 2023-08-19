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
@Table(name = "tournaments")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "date")
    private Date date;

    @Column(name = "status")
    @Enumerated(EnumType. STRING)
    private TournamentStatus status;
}
