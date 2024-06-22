package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.UserRoleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends ListCrudRepository<UserRoleEntity, UUID> {

    @Query("SELECT ur.role FROM UserRoleEntity ur WHERE ur.user.id=:iserId AND ur.club.id=:clubId")
    List<RoleEntity> getRolesByUserIdAndClubId(UUID userId, UUID clubId);

}
