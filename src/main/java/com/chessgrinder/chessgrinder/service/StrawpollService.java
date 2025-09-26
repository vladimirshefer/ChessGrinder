package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.enums.TournamentStatus;
import com.chessgrinder.chessgrinder.external.strawpoll.Poll;
import com.chessgrinder.chessgrinder.external.strawpoll.PollConfig;
import com.chessgrinder.chessgrinder.external.strawpoll.PollOptionDto;
import com.chessgrinder.chessgrinder.external.strawpoll.StrawpollClient;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import com.chessgrinder.chessgrinder.util.CacheUtil;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StrawpollService {

    private final ParticipantRepository participantRepository;
    private final TournamentRepository tournamentRepository;
    private final StrawpollClient strawpollClient;
    private final Map<UUID, String> cachedPollIds = CacheUtil.createCache(20);

    public String getOrCreateContestPollId(UUID tournamentId) {
        String cached = cachedPollIds.get(tournamentId);
        if (cached != null) {
            return cached;
        }

        TournamentEntity originalTournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NoSuchElementException("Tournament not found: " + tournamentId));
        List<TournamentEntity> sameEventTournaments = findSameEventTournaments(originalTournament);
        List<String> options = collectNicknames(sameEventTournaments);
        String title = buildContestTitle();

        synchronized (cachedPollIds) {
            String pollId = createPoll(title, options);
            for (TournamentEntity t : sameEventTournaments) {
                cachedPollIds.put(t.getId(), pollId);
            }
            return pollId;
        }
    }

    @Nullable
    public String getPollId(UUID tournamentId) {
        synchronized (cachedPollIds) {
            return cachedPollIds.get(tournamentId);
        }
    }

    private String createPoll(@Nonnull String title, List<String> options) {
        Poll poll = new Poll();
        poll.setTitle(title);
        List<PollOptionDto> optionDtos = options.stream()
                .map(PollOptionDto::new)
                .toList();
        poll.setPollOptions(optionDtos);
        PollConfig pollConfig = new PollConfig();
        pollConfig.setIsMultipleChoice(true);
        pollConfig.setMultipleChoiceMin(VOTES_PER_PERSON);
        pollConfig.setMultipleChoiceMax(VOTES_PER_PERSON);
        pollConfig.setRandomizeOptions(true);
        pollConfig.setDuplicationChecking("session");
        long tomorrowTimestamp = LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC);
        pollConfig.setDeadlineAt(tomorrowTimestamp);
        poll.setPollConfig(pollConfig);
        Poll poll1 = strawpollClient.createPoll(poll);
        return poll1.getId();
    }

    private List<String> collectNicknames(List<TournamentEntity> tournaments) {
        return tournaments.stream()
                .map(groupedTournament -> participantRepository.findByTournamentId(groupedTournament.getId()))
                .flatMap(Collection::stream)
                .map(ParticipantEntity::getNickname)
                .filter(nickname -> nickname != null && !nickname.isBlank())
                .toList();
    }

    private List<TournamentEntity> findSameEventTournaments(TournamentEntity baseTournament) {
        return tournamentRepository.findAllByStatus(TournamentStatus.ACTIVE)
                .stream()
                .filter(candidate -> Objects.equals(baseTournament.getCity(), candidate.getCity()))
                .filter(candidate -> Objects.equals(baseTournament.getDate().toLocalDate(), candidate.getDate().toLocalDate()))
                .collect(Collectors.toList());
    }

    private String buildContestTitle() {
        return "Nickname Contest " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private static final int VOTES_PER_PERSON = 3;

}
