package com.chessgrinder.chessgrinder.repositories;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import org.springframework.data.repository.*;
import org.springframework.data.rest.core.annotation.*;

@RepositoryRestResource
public interface UserBadgeRepository extends PagingAndSortingRepository<UserBadgeEntity, UUID>, CrudRepository<UserBadgeEntity, UUID> {

}
