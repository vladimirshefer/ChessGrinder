package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleRepository roleRepository;

    @GetMapping
    public Object getRoles(){
        return roleRepository.findAll().stream().map(role -> Map.of(
                "name", role.getName()
        ));
    }
}
