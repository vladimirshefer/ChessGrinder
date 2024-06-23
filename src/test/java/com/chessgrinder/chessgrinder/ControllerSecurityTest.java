package com.chessgrinder.chessgrinder;

import com.chessgrinder.chessgrinder.entities.UserEntity;
import com.chessgrinder.chessgrinder.repositories.UserRepository;
import com.chessgrinder.chessgrinder.security.CustomPermissionEvaluator;
import com.chessgrinder.chessgrinder.security.MyUserDetails;
import com.chessgrinder.chessgrinder.security.entitypermissionevaluator.EntityPermissionEvaluator;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultHttpSecurityExpressionHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableWebMvc
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(securedEnabled = true)
@SpringBootTest(
        classes = {
                ControllerSecurityTest.TestSpringConfiguration.class,
                ControllerSecurityTest.TestController.class,
                CustomPermissionEvaluator.class,
        }
)
@AutoConfigureMockMvc
public class ControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    private static final UUID FOO_ID = UUID.randomUUID();
    private static final UUID BAR_ID = UUID.randomUUID();
    private static final UUID ADMIN_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @SuppressWarnings("unused")
    @MockBean
    private UserRepository userRepository;

    @TestConfiguration
    public static class TestSpringConfiguration {

        /**
         * Disable default Spring security authorization via config.
         * Makes application be authorized only wia Method Security (@Secured and @PreAuthorize)
         */
        @Bean
        public static SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            return http
                    .authorizeHttpRequests(httpRequests -> httpRequests.anyRequest().permitAll())
                    .csrf(AbstractHttpConfigurer::disable)
                    .cors(AbstractHttpConfigurer::disable)
                    .oauth2Login(AbstractHttpConfigurer::disable)
                    .build();
        }

        @Bean
        public FooEntityPermissionEvaluator fooEntityPermissionEvaluator() {
            return new FooEntityPermissionEvaluator();
        }

        @Bean
        public static UserDetailsService getUserDetailsService() {
            return username -> {
                if ("admin".equalsIgnoreCase(username)) {
                    return new MyUserDetails(UserEntity.builder()
                            .id(ADMIN_ID)
                            .roles(Collections.emptyList())
                            .build()
                    );
                }
                if ("user".equalsIgnoreCase(username)) {
                    return new MyUserDetails(UserEntity.builder()
                            .id(USER_ID)
                            .roles(Collections.emptyList())
                            .build()
                    );
                }
                throw new InsufficientAuthenticationException("No user with username " + username + " for this test");
            };
        }

        @Bean
        public static MethodSecurityExpressionHandler methodSecurityExpressionHandler(CustomPermissionEvaluator permissionEvaluator) {
            var expressionHandler = new DefaultMethodSecurityExpressionHandler();
            expressionHandler.setPermissionEvaluator(permissionEvaluator);
            return expressionHandler;
        }

        @Bean
        public static DefaultHttpSecurityExpressionHandler httpSecurityExpressionHandler(CustomPermissionEvaluator permissionEvaluator) {
            var expressionHandler = new DefaultHttpSecurityExpressionHandler();
            expressionHandler.setPermissionEvaluator(permissionEvaluator);
            return expressionHandler;
        }

    }

    @RestController
    public static class TestController {

        @PreAuthorize("hasPermission(#id, 'FooEntity', 'ADMIN')")
        @PostMapping("/foo/{id}")
        public void createFoo(
                @PathVariable
                UUID id
        ) {
            Assertions.assertEquals(FOO_ID, id);
        }

        @PreAuthorize("hasPermission(#id, 'BarEntity', 'ADMIN')")
        @PostMapping("/bar/{id}")
        public void createBar(
                @PathVariable
                UUID id
        ) {
            Assertions.assertEquals(BAR_ID, id);
        }

//        @PreAuthorize("true")
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
    @Entity
    public static class FooEntity {
        private int value;
    }

    @Data
    @Getter
    @Entity
    public static class BarEntity {
        private int value;
    }

    public static class FooEntityPermissionEvaluator implements EntityPermissionEvaluator<FooEntity> {
        @Override
        public boolean hasPermission(UUID userId, FooEntity entity, String permission) {
            if (ADMIN_ID.equals(userId)) {
                return true;
            }
            if (USER_ID.equals(userId) && "USER".equalsIgnoreCase(permission)) {
                return true;
            }
            return false;
        }

        @Override
        public boolean hasPermission(UUID userId, String entityId, String permission) {
            if (ADMIN_ID.equals(userId)) {
                return true;
            }
            if (USER_ID.equals(userId) && "USER".equalsIgnoreCase(permission)) {
                return true;
            }
            return false;
        }
    }

    @Test
    @WithAnonymousUser
    public void testGetFoo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/foo/" + FOO_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithAnonymousUser
    public void testCreateFoo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/foo/" + FOO_ID))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "ADMIN")
    public void testCreateFooAsAdmin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/foo/" + FOO_ID))
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser
    public void testOtherControllersAreNotLoadedToTheTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user"))
                .andExpect(status().isNotFound());
    }

}
