package com.chessgrinder.chessgrinder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ControllerSecurityTest {

    private MockMvc mockMvc;
    private static final UUID FOO_ID = UUID.randomUUID();
    private static final UUID BAR_ID = UUID.randomUUID();

    @BeforeEach
    public void setUp(){
        this.mockMvc = MockMvcBuilders
//                .webAppContextSetup()
                .standaloneSetup(new TestController())
                .build();
    }

    @Test
    public void test() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/foo/" + FOO_ID))
                .andExpect(status().isOk());
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
    }

    public static class FooEntity {
    }

    public static class BarEntity {
    }


}
