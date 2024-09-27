package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserEloInitializerService {

    public static final int DEFAULT_ELO_POINTS = 1200;

    private final UserRepository userRepository;

}
