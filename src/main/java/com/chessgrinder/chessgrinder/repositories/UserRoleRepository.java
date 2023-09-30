package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.UserRoleEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface UserRoleRepository extends ListCrudRepository<UserRoleEntity, UUID> {
}
