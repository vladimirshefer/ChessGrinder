package com.chessgrinder.chessgrinder.service;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.pages.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.mappers.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class MainPageService {

    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final UserMapper userMapper;
    private final TournamentMapper tournamentMapper;

    public MainPageDto getInfoForMainPage() {

        List<User> users = (List<User>) userRepository.findAll();
        List<Tournament> tournaments = (List<Tournament>) tournamentRepository.findAll();

        return MainPageDto.builder()
                .users(userMapper.toDto(users))
                .tournaments(tournamentMapper.toDto(tournaments)).build();
    }
}
