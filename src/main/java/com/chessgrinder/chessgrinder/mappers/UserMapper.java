package com.chessgrinder.chessgrinder.mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.stereotype.*;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final BadgeRepository badgeRepository;
    private final BadgeMapper badgeMapper;
    private final ParticipantRepository participantRepository;

    public UserDto toDto(UserEntity user) {
        return toDto(user, null, null);
    }

    public UserDto toDto(UserEntity user, Date startSeason, Date endSeason) {
        List<BadgeEntity> userBadges = badgeRepository.getAllBadgesByUserId(user.getId());
        if (userBadges == null) {
            userBadges = Collections.emptyList();
        }
        final BigDecimal totalPoints = calcSeasonPoints(user.getId(), startSeason, endSeason);
        return UserDto.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .badges(badgeMapper.toDto(userBadges))
                .name(user.getName())
                .roles(user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toList()))
                .reputation(user.getReputation())
                .totalPoints(totalPoints)
                .build();
    }

    private BigDecimal calcSeasonPoints(UUID userId, Date startSeason, Date endSeason) {
        final List<ParticipantEntity> participants = participantRepository.findAllByUserId(userId);
        final boolean areDatesNull = startSeason == null || endSeason == null;
        return participants.stream()
                .filter(p -> {
                    final var tournament = p.getTournament();
                    if (tournament.getStatus() != TournamentStatus.FINISHED) {
                        return false;
                    }
                    if (areDatesNull) {
                        return true;
                    }
                    final LocalDateTime tournamentDateTime = tournament.getDate();
                    Date tournamentDate = Date.from(tournamentDateTime.toInstant(ZoneOffset.UTC));
                    return !tournamentDate.before(startSeason) && !tournamentDate.after(endSeason);
                })
                .map(ParticipantEntity::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<UserDto> toDto(List<UserEntity> users) {
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
