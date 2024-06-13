package com.chessgrinder.chessgrinder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ControllerSecurityTest {

    private MockMvc mockMvc;
    private UUID id = UUID.randomUUID();

    public static class TestController {

        @PreAuthorize("hasPermission(#id, 'FooTestEntity', 'ADMIN')")
        @PostMapping("/foo/{id}")
        public void createFoo(
                @PathVariable
                UUID id
        ) {
        }

        @PreAuthorize("hasPermission(#id, 'BarTestEntity', 'ADMIN')")
        @PostMapping("/bar/{id}")
        public void createBar(
                @PathVariable
                UUID id
        ) {
        }
    }

    public static class FooEntity {
    }

    public static class BarEntity {
    }

    @BeforeEach
    public void setUp(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(new TestController()).build();
    }

    @Test
    public void givenUserMemberInOrganization_whenGetOrganization_thenOK() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/foo/" + id))
                .andExpect(status().isOk());

//        Response response = givenAuth("john", "123").get("http://localhost:8082/organizations/1");
//        assertEquals(200, response.getStatusCode());
//        assertTrue(response.asString().contains("id"));
    }

//    private RequestSpecification givenAuth(String username, String password) {
//        FormAuthConfig formAuthConfig =
//                new FormAuthConfig("http://localhost:8082/login", "username", "password");
//
//        return RestAssured.given().auth().form(username, password, formAuthConfig);
//    }

    @Test
    void testGetFooSuccess() {

    }
}
