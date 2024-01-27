package com.chessgrinder.chessgrinder.service;

import java.util.*;
import java.util.stream.Collectors;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.dto.pages.*;
import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.enums.*;
import com.chessgrinder.chessgrinder.mappers.*;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class TournamentPageService {

    private final TournamentRepository tournamentRepository;
    private final ParticipantRepository participantRepository;
    private final RoundRepository roundRepository;
    private final MatchRepository matchRepository;

    private final TournamentMapper tournamentMapper;
    private final ParticipantMapper participantMapper;
    private final RoundMapper roundMapper;

    public TournamentPageDto getTournamentData(UUID tournamentId) {
        TournamentEntity tournamentEntity = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResponseStatusException(404, "No tournament with id " + tournamentId, null));
        List<ParticipantEntity> tournamentParticipantEntities = participantRepository.findByTournamentId(tournamentId);
        List<RoundEntity> tournamentRoundEntities = roundRepository.findByTournamentId(tournamentId);


        TournamentDto tournamentsDto = tournamentMapper.toDto(tournamentEntity);

        List<RoundDto> roundsDto = roundMapper.toDto(tournamentRoundEntities
                .stream()
                .sorted(Comparator.comparing(RoundEntity::getNumber))
                .collect(Collectors.toList()));

        List<ParticipantDto> participantsDto = participantMapper.toDto(tournamentParticipantEntities)
                .stream().sorted(Comparator.comparing(ParticipantDto::getScore)
                        .thenComparing((participant1, participant2) -> {

                            ParticipantDto winnerBetweenTwoParticipants = findWinnerBetweenTwoParticipants(participant1, participant2, roundsDto);

                            if (winnerBetweenTwoParticipants != null && winnerBetweenTwoParticipants.equals(participant1)) {
                                    return -1;
                                } else if (winnerBetweenTwoParticipants != null && winnerBetweenTwoParticipants.equals(participant2)) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                        })
                )
                .collect(Collectors.toList());

        return TournamentPageDto.builder()
                .tournament(tournamentsDto)
                .participants(participantsDto)
                .rounds(roundsDto)
                .build();
    }

    public ParticipantDto findWinnerBetweenTwoParticipants(ParticipantDto first, ParticipantDto second, List<RoundDto> roundsDto) {
        for (RoundDto round : roundsDto) {
            if (round.getMatches() == null) {
                continue;
            }

            for (MatchDto match : round.getMatches()) {
                if (match.getWhite() == null || match.getBlack() == null) {
                    continue;
                }

                if (isMatchBetweenParticipants(match, first, second)) {
                    return determineWinner(match);
                }

            }
        }

        return null;
    }

    private boolean isMatchBetweenParticipants(MatchDto match, ParticipantDto first, ParticipantDto second) {
        ParticipantDto wh = match.getWhite();
        ParticipantDto bl = match.getBlack();

        return wh.equals(first) && bl.equals(second) || wh.equals(second) && bl.equals(first);
    }

    private ParticipantDto determineWinner(MatchDto match) {
        if (match.getResult() == MatchResult.WHITE_WIN) {
            return match.getWhite();
        }

        if (match.getResult() == MatchResult.BLACK_WIN) {
            return match.getBlack();
        }

        return null;
    }

}
