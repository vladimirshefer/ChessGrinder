package com.chessgrinder.chessgrinder.entities;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@ToString
@Entity
@Table(name = "badges_table")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeEntity extends AbstractAuditingEntity {

    @Id
    @Column(name = "id")
    @UuidGenerator
    private UUID id;

    @Column(name = "titled")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "picture_url")
    private String pictureUrl;
}
