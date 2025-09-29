package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.external.strawpoll.Poll;
import com.chessgrinder.chessgrinder.external.strawpoll.PollOptionDto;
import com.chessgrinder.chessgrinder.external.strawpoll.StrawpollClient;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StrawpollServiceTest {

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private TournamentRepository tournamentRepository;

    @Mock
    private StrawpollClient strawpollClient;

    private StrawpollService strawpollService;

    @BeforeEach
    void setUp() {
        strawpollService = new StrawpollService(participantRepository, tournamentRepository, strawpollClient);
    }

    @Test
    void shouldGroupActiveTournamentsByCityAndDate() {
        // given
        LocalDateTime berlinDate = LocalDateTime.of(2024, 6, 10, 18, 0);

        TournamentEntity berlinOne = tournament(UUID.randomUUID(), "Berlin", berlinDate, "Berlin Open 1");
        TournamentEntity berlinTwoSameDay = tournament(UUID.randomUUID(), "Berlin", berlinDate.plusHours(2), "Berlin Open 2");
        TournamentEntity berlinOtherDay = tournament(UUID.randomUUID(), "Berlin", berlinDate.plusDays(1), "Berlin Open 3");
        TournamentEntity limassolSameDay = tournament(UUID.randomUUID(), "Limassol", berlinDate, "Limassol Blitz");

        when(tournamentRepository.findById(berlinOne.getId())).thenReturn(Optional.of(berlinOne));
        when(tournamentRepository.findAllByStatus(TournamentStatus.ACTIVE))
                .thenReturn(List.of(berlinOne, berlinTwoSameDay, berlinOtherDay, limassolSameDay));

        when(participantRepository.findByTournamentId(berlinOne.getId()))
                .thenReturn(participants(berlinOne, "Alpha", "Beta"));
        when(participantRepository.findByTournamentId(berlinTwoSameDay.getId()))
                .thenReturn(participants(berlinTwoSameDay, "Gamma"));

        Poll createdPoll = new Poll();
        createdPoll.setId("berlin-shared");
        when(strawpollClient.createPoll(any(Poll.class))).thenReturn(createdPoll);

        // when
        String pollId = strawpollService.getOrCreateContestPollId(berlinOne.getId());

        // then
        assertThat(pollId).isEqualTo("berlin-shared");

        ArgumentCaptor<Poll> pollCaptor = ArgumentCaptor.forClass(Poll.class);
        verify(strawpollClient, times(1)).createPoll(pollCaptor.capture());
        Poll generatedPoll = pollCaptor.getValue();

        List<String> optionValues = generatedPoll.getPollOptions().stream()
                .map(PollOptionDto::getValue)
                .toList();

        assertThat(optionValues).containsExactlyInAnyOrder("Alpha", "Beta", "Gamma");

        verify(participantRepository, times(1)).findByTournamentId(berlinOne.getId());
        verify(participantRepository, times(1)).findByTournamentId(berlinTwoSameDay.getId());
        verify(participantRepository, never()).findByTournamentId(berlinOtherDay.getId());
        verify(participantRepository, never()).findByTournamentId(limassolSameDay.getId());

        assertThat(strawpollService.getPollId(berlinOne.getId())).isEqualTo("berlin-shared");
        assertThat(strawpollService.getPollId(berlinTwoSameDay.getId())).isEqualTo("berlin-shared");
        assertThat(strawpollService.getPollId(berlinOtherDay.getId())).isNull();
        assertThat(strawpollService.getPollId(limassolSameDay.getId())).isNull();

        String cachedPollIdForSecondTournament = strawpollService.getOrCreateContestPollId(berlinTwoSameDay.getId());
        assertThat(cachedPollIdForSecondTournament).isEqualTo("berlin-shared");

        verify(tournamentRepository, times(1)).findAllByStatus(TournamentStatus.ACTIVE);
        verify(tournamentRepository, times(1)).findById(eq(berlinOne.getId()));
        verifyNoMoreInteractions(strawpollClient);
    }

    private TournamentEntity tournament(UUID id, String city, LocalDateTime date, String name) {
        return TournamentEntity.builder()
                .id(id)
                .city(city)
                .date(date)
                .name(name)
                .status(TournamentStatus.ACTIVE)
                .build();
    }

    private List<ParticipantEntity> participants(TournamentEntity tournament, String... nicknames) {
        List<ParticipantEntity> result = new java.util.ArrayList<>();
        int place = 1;
        for (String nickname : nicknames) {
            result.add(ParticipantEntity.builder()
                    .id(UUID.randomUUID())
                    .tournament(tournament)
                    .nickname(nickname)
                    .place(place++)
                    .build());
        }
        return result;
    }
}
