package com.chessgrinder.chessgrinder.security;

import com.chessgrinder.chessgrinder.repositories.ClubRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("securityService")
@RequiredArgsConstructor
public class SecurityService {
    private final ClubRepository clubRepository;

    public boolean hasClubRole(UUID clubId, String role) {
        // Your custom logic to check if the user has the specified role for the given club
        // Access Authentication for the logged-in user as needed
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        // Perform your custom role check logic here
        // For example:
        // return yourRoleService.hasRole(authentication.getName(), clubId, role);
        
        // Dummy implementation
        return true; // Replace with your actual logic
    }
}
