package com.chessgrinder.chessgrinder.configuration;

import com.chessgrinder.chessgrinder.external.strawpoll.StrawpollClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StrawpollConfiguration {

    @Bean
    StrawpollClient strawpollClient(
            @Value("${strawpoll.api-key:}")
            String apiKey
    ) {
        return new StrawpollClient(apiKey);
    }

}
