package com.chessgrinder.chessgrinder.service;

import com.chessgrinder.chessgrinder.entities.ParticipantEntity;
import com.chessgrinder.chessgrinder.entities.TournamentEntity;
import com.chessgrinder.chessgrinder.external.strawpoll.Poll;
import com.chessgrinder.chessgrinder.external.strawpoll.PollConfig;
import com.chessgrinder.chessgrinder.external.strawpoll.PollOptionDto;
import com.chessgrinder.chessgrinder.external.strawpoll.StrawpollClient;
import com.chessgrinder.chessgrinder.repositories.ParticipantRepository;
import com.chessgrinder.chessgrinder.repositories.TournamentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StrawpollService {

    @Value("${strawpoll.api-key}")
    private String strawpollApiKey;

    private final ParticipantRepository participantRepository;
    private final TournamentRepository tournamentRepository;
    private StrawpollClient strawpollClient;

    @PostConstruct
    public void init() {
        strawpollClient = new StrawpollClient(strawpollApiKey);
    }

    public String createStrawpoll(UUID tournamentID) {
        Optional<TournamentEntity> tournament = tournamentRepository.findById(tournamentID);
        String tournamentName = tournament.map(TournamentEntity::getName).orElse(null);
        List<ParticipantEntity> tournamentParticipants = participantRepository.findByTournamentId(tournamentID);
        List<String> nicknames = tournamentParticipants.stream().map(ParticipantEntity::getNickname).toList();
        return createPoll(tournamentName, nicknames);
    }

    public String createPoll(String title, List<String> options) {
        Poll poll = new Poll();
        poll.setTitle(title);
        List<PollOptionDto> optionDtos = options.stream()
                .map(value -> new PollOptionDto(value))
                .toList();
        poll.setPollOptions(optionDtos);
        PollConfig pollConfig = new PollConfig();
        pollConfig.setIsMultipleChoice(true);
        pollConfig.setMultipleChoiceMin(3);
        pollConfig.setMultipleChoiceMax(3);
        pollConfig.setRandomizeOptions(true);
        pollConfig.setDuplicationChecking("session");
        long tomorrowTimestamp = LocalDateTime.now().plusDays(1).toEpochSecond(ZoneOffset.UTC);
        pollConfig.setDeadlineAt(tomorrowTimestamp);
        poll.setPollConfig(pollConfig);
        Poll poll1 = strawpollClient.createPoll(poll);
        return "https://strawpoll.com/" + poll1.getId();
    }

}
