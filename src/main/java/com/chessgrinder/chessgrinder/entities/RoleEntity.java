package com.chessgrinder.chessgrinder.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "roles_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity extends AbstractAuditingEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(unique = true)
    private String name;

    @ToString.Exclude
    @ManyToMany(mappedBy = "roles")
    private List<UserEntity> users;

    public static class Roles {
        /**
         * - Can assign badges.
         * - Can create unlimited amount of tournaments.
         * - Can manage any tournament, including creating, deleting, and editing tournament details.
         */
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String USER = "ROLE_USER";
        /**
         * A trusted helper who runs tournaments together with the club owners, but is not a full admin.
         * In day-to-day life: when a user with this role joins a tournament (clicks "Participate", or is added by
         * another moderator), they automatically become a moderator of that tournament. There they can start and
         * finish it, create rounds, run the pairing, enter match results, mark players as missing and add or
         * remove participants. They cannot do this in tournaments they have not joined.
         */
        public static final String MODERATOR = "ROLE_MODERATOR";
    }

}
