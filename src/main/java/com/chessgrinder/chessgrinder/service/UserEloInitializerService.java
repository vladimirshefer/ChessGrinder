package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserEloInitializerService {

    private static final int DEFAULT_ELO_POINTS = 1200;

    private final UserRepository userRepository;

    public void setDefaultEloIfNeeded(UserEntity user, boolean isAuthorized) {
        if (isAuthorized && user.getEloPoints() == 0) {
            user.setEloPoints(DEFAULT_ELO_POINTS);
            userRepository.save(user);
        }
    }
}
