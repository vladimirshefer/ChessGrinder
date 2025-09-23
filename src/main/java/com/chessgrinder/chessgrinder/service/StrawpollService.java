package com.chessgrinder.chessgrinder.service;

import java.util.*;

import com.chessgrinder.chessgrinder.entities.*;
import com.chessgrinder.chessgrinder.external.strawpoll.Poll;
import com.chessgrinder.chessgrinder.external.strawpoll.PollConfig;
import com.chessgrinder.chessgrinder.external.strawpoll.PollOptionDto;
import com.chessgrinder.chessgrinder.repositories.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
public class StrawpollService {

    @Value("${strawpoll.api-key}")
    private String strawpollApiKey;

    private final ParticipantRepository participantRepository;
    private final TournamentRepository tournamentRepository;

    public String createStrawpoll(UUID tournamentID) {
        Optional<TournamentEntity> tournament = tournamentRepository.findById(tournamentID);
        String tournamentName = tournament.map(TournamentEntity::getName).orElse(null);
        List<ParticipantEntity> tournamentParticipants = participantRepository.findByTournamentId(tournamentID);
        List<String> nicknames = tournamentParticipants.stream().map(ParticipantEntity::getNickname).toList();
        String strawpollLink = createStrawpollLink(tournamentName, nicknames);
        return strawpollLink;
    }

    public String createStrawpollLink(String title, List<String> options) {
        RestTemplate restTemplate = new RestTemplate();

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
        poll.setPollConfig(pollConfig);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("API-KEY", strawpollApiKey);
        HttpEntity<Poll> entity = new HttpEntity<>(poll, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.strawpoll.com/v3/polls", entity, Map.class);

            return response.getBody().get("url").toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Strawpoll", e);
        }
    }

}
