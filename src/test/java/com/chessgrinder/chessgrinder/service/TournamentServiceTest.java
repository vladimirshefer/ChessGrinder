package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.dto.TournamentDto;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.RepeatableType;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.mappers.TournamentMapper;
import com.chessgrinder.chessgrinder.repositories.RoundRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TournamentServiceTest {

    private TournamentRepository tournamentRepository;

    private TournamentService tournamentService;

    @BeforeEach
    void setUp() {
        tournamentRepository = Mockito.mock(TournamentRepository.class);
        tournamentService = new TournamentService(
                tournamentRepository,
                Mockito.mock(RoundRepository.class),
                List.of(),
                new TournamentMapper(),
                Mockito.mock(RoundService.class)
        );
    }

    @Test
    void updateTournament_shouldPersistRepeatable() {
        UUID tournamentId = UUID.randomUUID();
        TournamentEntity tournament = TournamentEntity.builder()
                .id(tournamentId)
                .status(TournamentStatus.PLANNED)
                .roundsNumber(6)
                .build();
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        tournamentService.updateTournament(tournamentId, dto(RepeatableType.WEEKLY));

        ArgumentCaptor<TournamentEntity> saved = ArgumentCaptor.forClass(TournamentEntity.class);
        verify(tournamentRepository).save(saved.capture());
        assertThat(saved.getValue().getRepeatable()).isEqualTo(RepeatableType.WEEKLY);
    }

    @Test
    void updateTournament_shouldClearRepeatable() {
        UUID tournamentId = UUID.randomUUID();
        TournamentEntity tournament = TournamentEntity.builder()
                .id(tournamentId)
                .status(TournamentStatus.PLANNED)
                .roundsNumber(6)
                .repeatable(RepeatableType.WEEKLY)
                .build();
        when(tournamentRepository.findById(tournamentId)).thenReturn(Optional.of(tournament));

        tournamentService.updateTournament(tournamentId, dto(null));

        ArgumentCaptor<TournamentEntity> saved = ArgumentCaptor.forClass(TournamentEntity.class);
        verify(tournamentRepository).save(saved.capture());
        assertThat(saved.getValue().getRepeatable()).isNull();
    }

    private static TournamentDto dto(RepeatableType repeatable) {
        return TournamentDto.builder()
                .name("Thursday Blitz")
                .date(LocalDateTime.now().plusDays(1))
                .roundsNumber(6)
                .repeatable(repeatable)
                .build();
    }
}
