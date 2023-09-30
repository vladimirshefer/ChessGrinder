package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.UserBadgeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.UUID;

public interface UserBadgeRepository extends PagingAndSortingRepository<UserBadgeEntity, UUID>, CrudRepository<UserBadgeEntity, UUID> {

}
