package com.chessgrinder.chessgrinder.external.strawpoll;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class StrawpollClient {
    public static final String HOST = "https://api.strawpoll.com";
    public static final String BASE_URL = HOST + "/v3";
    private static final String HEADER_API_KEY = "X-API-KEY";

    private final RestTemplate restTemplate;

    public StrawpollClient(String token) {
        restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            request.getHeaders().set(HEADER_API_KEY, token);
            return execution.execute(request, body);
        });
    }

    public Poll getPoll(String pollId) {
        return restTemplate.getForObject(BASE_URL + "/polls/" + pollId, Poll.class);
    }

    public Poll createPoll(Poll poll) {
        ResponseEntity<Poll> response = restTemplate.postForEntity(HOST + "/v3/polls", poll, Poll.class);
        return response.getBody();
    }

}
