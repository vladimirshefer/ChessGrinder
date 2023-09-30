package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.RoleEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends ListCrudRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByName(String name);

}
