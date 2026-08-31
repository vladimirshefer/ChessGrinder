package com.chessgrinder.chessgrinder.repositories;

import com.chessgrinder.chessgrinder.entities.FileEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface FileRepository extends PagingAndSortingRepository<FileEntity, UUID>, ListCrudRepository<FileEntity, UUID> {
    
    List<FileEntity> findByUserId(UUID userId);
    
    @Query("SELECT f FROM FileEntity f WHERE f.user.id = :userId AND f.purpose = :purpose")
    List<FileEntity> findByUserIdAndPurpose(UUID userId, String purpose);
    
    List<FileEntity> findByPurpose(String purpose);
}