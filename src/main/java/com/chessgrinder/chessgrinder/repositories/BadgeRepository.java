package com.chessgrinder.chessgrinder.repositories;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import org.springframework.data.repository.*;
import org.springframework.data.rest.core.annotation.*;

@RepositoryRestResource
public interface BadgeRepository extends PagingAndSortingRepository<Badge, UUID>, CrudRepository<Badge, UUID> {
}
