package com.chessgrinder.chessgrinder.security;

import com.chessgrinder.chessgrinder.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@SuppressWarnings("Convert2MethodRef")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
@Slf4j
public class WebSecurityConfig {

    public static final String HOME_PAGE = "/";

    public static final String OAUTH2_STATE_SEPARATOR = ",";

    @Autowired
    private CustomOAuth2UserService oauthUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private WithRefererOAuth2AuthorizationRequestResolver oAuth2authorizationRequestResolver;

    @Bean
    static MethodSecurityExpressionHandler expressionHandler(CustomPermissionEvaluator permissionEvaluator) {
        var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(httpRequests -> httpRequests
                        .anyRequest().permitAll()
                )
                .formLogin(it -> it
                        // to void redirect to default http spring endpoint
                        .successHandler(
                                (req, res, auth) -> res.setStatus(HttpStatus.NO_CONTENT.value())
                        )
                )
                .logout(it -> it
                        // to void redirect to default http spring endpoint
                        .logoutSuccessHandler(
                                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)
                        )
                )
                .httpBasic(it -> it.disable())
                .oauth2Login(oauth2Login ->
                        oauth2Login
                                .authorizationEndpoint(it -> it.authorizationRequestResolver(oAuth2authorizationRequestResolver))
                                .userInfoEndpoint(it -> it.userService(oauthUserService))
                                .successHandler((request, response, authentication) -> {
                                    log.debug("Successfully authenticated user " + authentication.getName() + " via oauth2");
                                    if (authentication.getPrincipal() instanceof CustomOAuth2User customOAuth2User) {
                                        userService.processOAuthPostLogin(customOAuth2User);
                                    }
                                    String redirectTo = HOME_PAGE;
                                    String state = request.getParameter("state");
                                    if (StringUtils.isNotBlank(state)) {
                                        String[] stateSplit = state.split(OAUTH2_STATE_SEPARATOR);
                                        if (stateSplit.length >= 2) {
                                            redirectTo = stateSplit[1];
                                        }
                                    }
                                    response.sendRedirect(redirectTo);
                                })
                                .failureHandler((request, response, exception) -> {
                                    log.warn("Could not login user vis oauth2", exception);
                                    response.sendRedirect(HOME_PAGE);
                                })
                )
                .cors(it -> it.disable())
                .csrf(it -> it.disable())
                .exceptionHandling(it -> it.defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), new AntPathRequestMatcher("/**")))
                .build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
