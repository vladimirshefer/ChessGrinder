package com.chessgrinder.chessgrinder.entities;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "badges")
@Slf4j
public class Badge {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "titled")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "picture_url")
    private String pictureUrl;

}
