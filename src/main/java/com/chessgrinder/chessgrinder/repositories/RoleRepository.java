package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.Role;
import org.springframework.data.repository.ListCrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends ListCrudRepository<Role, UUID> {

    Optional<Role> findByName(String name);

}
