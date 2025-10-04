package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.mappers.ParticipantMapper;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.entitypermissionevaluator.EntityPermissionEvaluator;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

import static com.chessgrinder.chessgrinder.enums.TournamentStatus.PLANNED;
import static com.chessgrinder.chessgrinder.security.entitypermissionevaluator.TournamentEntityPermissionEvaluatorImpl.Permissions.MODERATOR;
import static com.chessgrinder.chessgrinder.security.entitypermissionevaluator.TournamentEntityPermissionEvaluatorImpl.Permissions.OWNER;

@Component
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final TournamentRepository tournamentRepository;
    private final ParticipantMapper participantMapper;
    private final UserRepository userRepository;
    private final EntityPermissionEvaluator<TournamentEntity> tournamentPermissionEvaluator;

    public void addParticipantToTheTournament(UUID tournamentId, ParticipantDto participantDto) {

        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentId).orElseThrow(); //TODO Exception
        UserEntity userEntity = null;
        if (participantDto.getUserId() != null) {
            userEntity = userRepository.findById(UUID.fromString(participantDto.getUserId())).orElse(null);
        }

        ParticipantEntity participantEntity = ParticipantEntity.builder()
                .tournament(tournamentEntity)
                .nickname(participantDto.getName())
                .user(userEntity)
                .score(BigDecimal.ZERO)
                .buchholz(BigDecimal.ZERO)
                .place(-1)
                .build();

        participantRepository.save(participantEntity);
    }

    public void delete(UUID participantId) {
        ParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new NoSuchElementException("No participant with id " + participantId));
        if (participant != null) {
            participantRepository.delete(participant);
        }
    }

    public ParticipantDto get(UUID participantId) {
        ParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new NoSuchElementException("No participant with id " + participantId));
        return participantMapper.toDto(participant);
    }

    @Nullable
    public List<ParticipantDto> getWinner(UUID tournamentId) {
        return participantMapper.toDto(participantRepository.findAllWinnersByTournamentId(tournamentId));
    }

    public void update(UUID tournamentId, UUID participantId, UserEntity user, ParticipantDto participantDto) {
        ParticipantEntity participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No participant with id " + participantId));
        TournamentEntity tournament = tournamentRepository.findById(tournamentId).orElseThrow();

        boolean isMeModerator = tournamentPermissionEvaluator.hasPermission(user, tournamentId.toString(), MODERATOR.name());
        boolean isMyParticipant = participant.getUser() != null && participant.getUser().getId().equals(user.getId());
        boolean isTournamentNotStarted = tournament.getStatus().equals(PLANNED);
        boolean canChangeNickname = isMeModerator || (isMyParticipant && isTournamentNotStarted);
        // This is partial DTO, therefore, name could be null.
        //noinspection ConstantValue
        if (participantDto.getName() != null && !Objects.equals(participant.getNickname(), participantDto.getName())) {
            if (canChangeNickname) {
                participant.setNickname(participantDto.getName());
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to change the nickname of this participant.");
            }
        }

        boolean isMeOwner = tournamentPermissionEvaluator.hasPermission(user, tournamentId.toString(), OWNER.name());
        if (participantDto.getIsModerator() != null && participant.isModerator() != participantDto.getIsModerator()) {
            if (isMeOwner) {
                participant.setModerator(participantDto.getIsModerator());
            } else {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to change the moderator status of this participant.");
            }
        }

        participantRepository.save(participant);
    }
}
