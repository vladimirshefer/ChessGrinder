package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleEntity getOrCreate(String role) {
        return roleRepository.findByName(role)
                .orElseGet(() -> roleRepository.save(RoleEntity.builder().name(role).build()));
    }

}
