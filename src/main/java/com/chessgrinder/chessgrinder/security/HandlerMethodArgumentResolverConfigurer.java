package com.chessgrinder.chessgrinder.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Registers this argument handler to the MVC context.
 * Source: https://reflectoring.io/spring-boot-argumentresolver/
 */
@Component
@RequiredArgsConstructor
public class HandlerMethodArgumentResolverConfigurer implements WebMvcConfigurer {
    private final AuthenticatedUserArgumentResolver resolver;

    @Override
    public void addArgumentResolvers(
            List<HandlerMethodArgumentResolver> resolvers
    ) {
        resolvers.add(0, resolver);
    }
}
