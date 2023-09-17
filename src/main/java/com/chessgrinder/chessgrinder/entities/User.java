package com.chessgrinder.chessgrinder.entities;

import java.util.*;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.*;

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
    private UUID id;

    /**
     * User login. Unique across all users. E.g. "vshefer".
     * Could be null for "guest" users.
     */
    @Column(name = "username")
    @Nullable
    private String username;

    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Hash of the user.
     */
    @Column(name = "password")
    @Nullable
    private String password;

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin = false;
}
