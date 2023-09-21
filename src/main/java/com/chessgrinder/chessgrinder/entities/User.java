package com.chessgrinder.chessgrinder.entities;

import java.util.*;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Setter
@ToString
@Entity
@Table(name = "users")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
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
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

    public enum Provider {
        GUEST, LOCAL, GOOGLE, GITHUB
    }
}
