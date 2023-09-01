package com.chessgrinder.chessgrinder.configuration;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.web.*;

@Configuration
public class CorsConfiguration {
    @Bean
    public SecurityFilterChain corsAndCsrfDisableChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
