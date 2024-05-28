package com.sotnikov.ListToDoBackend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = JWTFilterTest.TestController.class)
@AutoConfigureDataMongo
class JWTFilterTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private JWTFilter jwtFilter;

    @Test
    public void testJwtFilterWhenTokenHasBearerButNotCorrect() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer");

        ResultActions resultActions = mvc.perform(
                get("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
        );

        resultActions.andExpect(status().isUnauthorized());


    }

    @Test
    public void testJwtFilterWhenTokenIsBlank() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "    \n");

        ResultActions resultActions = mvc.perform(
                get("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
        );

        resultActions.andExpect(status().isBadRequest());
    }

//    @Test
//    public void test

    @RestController
    static class TestController {
        @GetMapping("/")
        public String get() {
            return "got it";
        }
    }
}