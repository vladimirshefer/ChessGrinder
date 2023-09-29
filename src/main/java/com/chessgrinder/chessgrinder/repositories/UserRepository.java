package com.chessgrinder.chessgrinder.repositories;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import org.springframework.data.repository.*;
import org.springframework.data.rest.core.annotation.*;

@RepositoryRestResource
public interface UserRepository extends PagingAndSortingRepository<UserEntity, UUID>, CrudRepository<UserEntity, UUID> {

    @Override
    List<UserEntity> findAll();

    UserEntity findByUsername(String userName);

}
