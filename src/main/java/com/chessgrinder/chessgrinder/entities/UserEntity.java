package com.chessgrinder.chessgrinder.entities;

import java.math.BigDecimal;
import java.util.*;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UuidGenerator;

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
     * User login. Unique across all users. E.g. "vshefer".
     * Could be null for "guest" users.
     */
    @Column(name = "username", unique = true)
    @Nullable
    private String username;

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
    private Provider provider;

    @ManyToMany
    @Fetch(FetchMode.JOIN)
    @ToString.Exclude
    @JoinTable(
            name = "users_roles_table",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<RoleEntity> roles;

    @Column(name = "reputation")
    private int reputation = 0;

    @Transient //won't be created in DB
    private BigDecimal globalScore = BigDecimal.valueOf(-1);

    public enum Provider {
        GUEST, LOCAL, GOOGLE, GITHUB
    }
}
