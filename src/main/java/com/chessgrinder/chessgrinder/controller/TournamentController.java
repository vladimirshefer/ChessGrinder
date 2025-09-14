package com.chessgrinder.chessgrinder.controller;

import com.chessgrinder.chessgrinder.chessengine.pairings.JaVaFoPairingStrategyImpl;
import com.chessgrinder.chessgrinder.dto.ListDto;
import com.chessgrinder.chessgrinder.dto.MatchDto;
import com.chessgrinder.chessgrinder.dto.ParticipantDto;
import com.chessgrinder.chessgrinder.dto.TournamentDto;
import com.chessgrinder.chessgrinder.entities.MatchEntity;
import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.RoleEntity;
import com.chessgrinder.chessgrinder.entities.RoundEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.mappers.MatchMapper;
import com.chessgrinder.chessgrinder.mappers.ParticipantMapper;
import com.chessgrinder.chessgrinder.mappers.TournamentMapper;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.security.AuthenticatedUserArgumentResolver.AuthenticatedUser;
import com.chessgrinder.chessgrinder.security.util.SecurityUtil;
import com.chessgrinder.chessgrinder.service.TournamentService;
import com.chessgrinder.chessgrinder.chessengine.trf.util.TrfUtil;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.MissingPlayersXxzTrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.Player001TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.TrfLine;
import com.chessgrinder.chessgrinder.chessengine.trf.dto.RoundsNumberXxrTrfLine;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.chessgrinder.chessgrinder.comparator.ParticipantDtoComparators.COMPARE_PARTICIPANT_DTO_BY_NICKNAME_NULLS_LAST;

@RestController
@RequestMapping("/tournament")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;
    private final TournamentRepository tournamentRepository;
    private final ParticipantRepository participantRepository;
    private final TournamentMapper tournamentMapper;
    private final MatchMapper matchMapper;
    private final ParticipantMapper participantMapper;

    @Secured(RoleEntity.Roles.ADMIN)
    @PostMapping
    public TournamentDto createTournament(
            @AuthenticatedUser UserEntity owner
    ) {
        return tournamentService.createTournament(LocalDateTime.now(), owner);
    }

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @GetMapping("/{tournamentId}/action/start")
    public void startTournament(
            @PathVariable UUID tournamentId
    ) {
        tournamentService.startTournament(tournamentId);
    }

    @PreAuthorize("hasPermission(#tournamentId,'TournamentEntity','MODERATOR')")
    @GetMapping("/{tournamentId}/action/finish")
    public void finishTournament(@PathVariable UUID tournamentId) {
        tournamentService.finishTournament(tournamentId);
    }

    @GetMapping
    public Object getTournaments() {
        return ListDto.of(tournamentService.findTournaments());
    }

    @GetMapping("/{tournamentId}")
    public TournamentDto getTournament(
            @PathVariable UUID tournamentId
    ) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(404, "No tournament with id " + tournamentId, null));
        return tournamentMapper.toDto(tournamentEntity);
    }

    @GetMapping(value = "/{tournamentId}/export/trf", produces = "text/plain")
    public String getTournamentExportTrf(
            @PathVariable UUID tournamentId
    ) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(404, "No tournament with id " + tournamentId, null));

        List<ParticipantEntity> participantEntities = participantRepository.findByTournamentId(tournamentId);
        List<ParticipantDto> participantDtos = participantMapper.toDto(participantEntities);

        List<List<MatchEntity>> allMatchesInTheTournament = tournament.getRounds().stream()
                .filter(RoundEntity::isFinished)
                .map(RoundEntity::getMatches)
                .toList();

        List<List<MatchDto>> allMatches = allMatchesInTheTournament.stream().map(matchMapper::toDto).toList();
        var matchHistory = allMatches;
        var participants = participantDtos;
        var roundsNumber = tournament.getRoundsNumber();

        Map<ParticipantDto, List<MatchDto>> participantsMatches = JaVaFoPairingStrategyImpl.getParticipantsMatches(participants, matchHistory);
        List<String> playerIds = participantsMatches.keySet().stream()
                .sorted(COMPARE_PARTICIPANT_DTO_BY_NICKNAME_NULLS_LAST)
                .map(ParticipantDto::getId).toList();

        List<TrfLine> trfLines = new ArrayList<>();
        // XXR - number of rounds. required for pairing.
        trfLines.add(RoundsNumberXxrTrfLine.builder().roundsNumber(roundsNumber).build());

        List<ParticipantDto> missingParticipants = participants.stream().filter(ParticipantDto::getIsMissing).toList();
        trfLines.add(
                MissingPlayersXxzTrfLine.builder()
                        .playerIds(missingParticipants.stream().map(it1 -> playerIds.indexOf(it1.getId()) + 1).toList())
                        .build()
        );

        List<Player001TrfLine> playerTrfLines = participantsMatches.entrySet()
                .stream()
                .map(entry -> JaVaFoPairingStrategyImpl.toTrfDto(entry.getKey(), entry.getValue(), playerIds))
                .toList();

        trfLines.addAll(playerTrfLines);

        String tournamentTrf = TrfUtil.writeTrfLines(trfLines);

        return tournamentTrf;
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @PutMapping("/{tournamentId}")
    public void updateTournament(
            @PathVariable UUID tournamentId,
            @RequestBody TournamentDto tournamentDto
    ) {
        tournamentService.updateTournament(tournamentId, tournamentDto);
    }

    @Secured(RoleEntity.Roles.ADMIN)
    @DeleteMapping("/{tournamentId}")
    public void deleteTournament(@PathVariable UUID tournamentId) {
        tournamentService.deleteTournament(tournamentId);
    }

    @PostMapping("{tournamentId}/action/participate")
    @Transactional
    public void participate(
            @PathVariable
            UUID tournamentId,
            @RequestParam(required = false)
            @Nullable
            String nickname,
            @AuthenticatedUser UserEntity user
    ) {
        TournamentEntity tournament = tournamentRepository.findById(tournamentId).orElseThrow();

        if (!tournament.getStatus().equals(TournamentStatus.PLANNED)) {
            throw new ResponseStatusException(400, "Tournament already started. Ask administrator for help.", null);
        }

        if (tournament.getRegistrationLimit() != null && tournament.getRegistrationLimit() > 0 && tournament.getRegistrationLimit() <= participantRepository.countByTournament(tournamentId)) {
            throw new ResponseStatusException(400, "Participant limit is full.", null);
        }

        ParticipantEntity participant = ParticipantEntity.builder()
                .tournament(tournament)
                .user(user)
                .nickname(nickname)
                .score(BigDecimal.ZERO)
                .buchholz(BigDecimal.ZERO)
                .place(0)
                .build();

        if (SecurityUtil.hasRole(user, RoleEntity.Roles.ADMIN)) {
            participant.setModerator(true);
        }

        participantRepository.save(participant);
    }
}
