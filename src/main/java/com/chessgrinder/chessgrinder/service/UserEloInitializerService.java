package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserEloInitializerService {

    private static final int DEFAULT_ELO_POINTS = 1200;

    private final UserRepository userRepository;

    @Autowired
    public UserEloInitializerService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isAuthorizedUser(UserEntity user) {
        return user != null && user.getId() != null;
    }


    public void setDefaultEloIfNeeded(UserEntity user, boolean isAuthorized) {
        if (isAuthorized && user.getEloPoints() == 0) {
            user.setEloPoints(DEFAULT_ELO_POINTS);
            userRepository.save(user);
        }
    }
}
