package com.chessgrinder.chessgrinder.service;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    public List<UserDto> getInfoForMainPage() {

        List<UserBadge> allUserBadges = (List<UserBadge>) userBadgeRepository.findAll();

        System.out.println("lol");

        return null;
    }
}
