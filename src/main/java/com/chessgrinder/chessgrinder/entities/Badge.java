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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Badge {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "titled")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "picture_url")
    private String pictureUrl;

}
