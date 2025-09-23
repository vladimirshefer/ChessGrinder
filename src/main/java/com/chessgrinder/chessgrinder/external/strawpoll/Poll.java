package com.chessgrinder.chessgrinder.external.strawpoll;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

/**
 * https://strawpoll.com/docs/api/strawpoll-api-v3.html#/schemas/Poll
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Poll {
    private String id;
    private String title;
    private List<PollOptionDto> pollOptions;
    private PollConfig pollConfig;
}
