package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.entities.UserRoleEntity;
import com.chessgrinder.chessgrinder.repositories.RoleRepository;
import com.chessgrinder.chessgrinder.repositories.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleEntity getOrCreate(String role) {
        return roleRepository.findByName(role)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder().name(role).build()));
    }

    public void assignRole(UserEntity user, String role) {
        RoleEntity roleEntity = getOrCreate(role);
        userRoleRepository.save(UserRoleEntity.builder()
                .user(user)
                .role(roleEntity)
                .build()
        );
    }

}
