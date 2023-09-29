package com.chessgrinder.chessgrinder.repositories;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import org.springframework.data.repository.*;

public interface RoleRepository extends ListCrudRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByName(String name);

}
