package com.chessgrinder.chessgrinder.service;

import java.util.*;

import com.chessgrinder.chessgrinder.dto.*;
import com.chessgrinder.chessgrinder.entities.*;
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

    @Value("${telegram.bot-token}")
    private String telegramBotToken;

    @Value("${telegram.chat-id}")
    private String telegramChatId;


    private final ParticipantRepository participantRepository;
    private final TournamentRepository tournamentRepository;

    public String createStrawpoll(UUID tournamentID) {
        Optional<TournamentEntity> tournament = tournamentRepository.findById(tournamentID);
        String tournamentName = tournament.map(TournamentEntity::getName).orElse(null);
        List<ParticipantEntity> tournamentParticipants = participantRepository.findByTournamentId(tournamentID);
        List<String> nicknames = tournamentParticipants.stream().map(ParticipantEntity::getNickname).toList();
        String strawpollLink = createStrawpollLink(tournamentName, nicknames);
        sendToTelegram(strawpollLink);
        return strawpollLink;
    }

    public String createStrawpollLink(String title, List<String> options) {
        RestTemplate restTemplate = new RestTemplate();

        StrawpollRequestDto request = new StrawpollRequestDto();
        request.setTitle(title);

        List<PollOptionDto> optionDtos = options.stream()
                .map(PollOptionDto::new)
                .toList();
        request.setOptions(optionDtos);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("API-KEY", strawpollApiKey);
        HttpEntity<StrawpollRequestDto> entity = new HttpEntity<>(request, headers);
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://api.strawpoll.com/v3/polls", entity, Map.class);

            return response.getBody().get("url").toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Strawpoll", e);
        }
    }

    public void sendToTelegram(String messageText) {
        String url = "https://api.telegram.org/bot" + telegramBotToken + "/sendMessage";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> request = new HashMap<>();
        request.put("chat_id", telegramChatId);
        request.put("text", messageText);

        restTemplate.postForObject(url, request, String.class);
    }
}
