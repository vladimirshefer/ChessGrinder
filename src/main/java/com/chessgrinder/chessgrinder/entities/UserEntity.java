package com.chessgrinder.chessgrinder.entities;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(name = "users_table")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends AbstractAuditingEntity {
    @Id
    @Column(name = "id", nullable = false)
    @UuidGenerator
    private UUID id;

    /**
     * User login (email or provider login). Unique across all users.
     * Note: historically named "username", but actually stores email/login used for authentication.
     */
    @Column(name = "username", unique = true)
    @Nullable
    private String username;

    /**
     * Public user tag (handle) to be used in profile URLs. E.g. "vshefer".
     * Nullable: user may not have selected a public handle.
     */
    @Column(name = "usertag", unique = true)
    @Nullable
    private String usertag;

    @Nullable
    @Column(name = "name")
    private String name;

    /**
     * Hash of the user.
     */
    @Column(name = "password")
    @Nullable
    private String password;

    @Enumerated(EnumType.STRING)
    @Nullable
    private Provider provider;

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.JOIN)
    @ToString.Exclude
    @JoinTable(
            name = "users_roles_table",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<RoleEntity> roles;

    @Builder.Default
    @Column(name = "reputation")
    private int reputation = 0;

    @Column(name = "elo_points")
    private int eloPoints = 0;

    public enum Provider {
        GUEST, LOCAL, GOOGLE, GITHUB, CHESSCOM
    }

    public static final String USERTAG_REGEX = "^[a-zA-Z][a-zA-Z0-9]{1,30}$";

}
