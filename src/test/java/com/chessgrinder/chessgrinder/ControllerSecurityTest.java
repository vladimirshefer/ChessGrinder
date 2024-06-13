package com.chessgrinder.chessgrinder;

import lombok.Data;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableWebMvc
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = false)
@SpringBootTest(
        classes = {
                ControllerSecurityTest.PermitAllFilterChainSecurityConfig.class,
                ControllerSecurityTest.TestController.class
        }
)
public class ControllerSecurityTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private static final UUID FOO_ID = UUID.randomUUID();
    private static final UUID BAR_ID = UUID.randomUUID();

    @Configuration
    public static class PermitAllFilterChainSecurityConfig {

        /**
         * Disable default Spring security authorization via config.
         * Makes application be authorized only wia Method Security (@Secured and @PreAuthorize)
         */
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            return http.authorizeHttpRequests(httpRequests -> httpRequests.anyRequest().permitAll()).build();
        }
    }

    @RestController
    public static class TestController {

        @PreAuthorize("hasPermission(#id, 'FooTestEntity', 'ADMIN')")
        @PostMapping("/foo/{id}")
        public void createFoo(
                @PathVariable
                UUID id
        ) {
            Assertions.assertEquals(FOO_ID, id);
        }

        @PreAuthorize("hasPermission(#id, 'BarTestEntity', 'ADMIN')")
        @PostMapping("/bar/{id}")
        public void createBar(
                @PathVariable
                UUID id
        ) {
            Assertions.assertEquals(BAR_ID, id);
        }

        @GetMapping(value = "/foo/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
        public FooEntity getFoo(
                @PathVariable
                UUID id
        ) {
            Assertions.assertEquals(FOO_ID, id);
            return new FooEntity();
        }
    }

    @Data
    @Getter
    public static class FooEntity {
        private int value;
    }

    @Data
    @Getter
    public static class BarEntity {
        private int value;
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }


    @Test
    public void testCreateFoo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/foo/" + FOO_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetFoo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/foo/" + FOO_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testOtherControllersAreNotLoadedToTheTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(status().isNotFound());
    }

}
