package com.chessgrinder.chessgrinder.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.UuidGenerator;

import javax.annotation.Nullable;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(name = "users_roles_table")
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleEntity extends AbstractAuditingEntity {

    @Id
    @Column(name = "id", nullable = false)
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name="role_id", nullable = false)
    private RoleEntity role;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = true)
    @Nullable
    private ClubEntity club;

}
