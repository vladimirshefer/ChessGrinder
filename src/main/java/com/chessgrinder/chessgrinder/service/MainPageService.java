package com.chessgrinder.chessgrinder.service;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class MainPageService {

    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;

    public MainPageDto getInfoForMainPage() {

        List<UUID> userIds = ((List<UserBadge>) userBadgeRepository.findAll()).stream()
                .map(userBadge -> userBadge.getUser().getId()).toList();

        List<User> users = (List<User>) userRepository.findAllById(userIds);
        List<UserDto> userDtos = new ArrayList<>();

        for (User user: users) {
            List<BadgeDto> listBadgesDto = badgeRepository.getAllBadgesByUserId(user.getId()).stream()
                    .map(badge -> BadgeDto.builder()
                                    .title(badge.getTitle())
                                    .description(badge.getDescription())
                                    .pictureUrl(badge.getPictureUrl())
                                    .build())
                    .toList();

            userDtos.add(UserDto.builder().userBadges(listBadgesDto).name(user.getName()).build());
        }

        List<TournamentDto> tournamentDtos = ((List<Tournament>) tournamentRepository.findAll()).stream()
                .map(tournament -> TournamentDto.builder()
                                     .date(tournament.getDate())
                                     .status(tournament.getStatus())
                                     .build())
                .toList();

        return MainPageDto.builder().users(userDtos).tournaments(tournamentDtos).build();
    }
}
