package com.sotnikov.ListToDoBackend.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotnikov.ListToDoBackend.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureDataMongo
@ExtendWith(MockitoExtension.class)
class JWTFilterTest {

    private MockMvc mvc;

    private JWTUtil jwtUtil;
    private UserDetailsService userDetailsService;

    private User user;
    private UserDetailsImpl userDetails;

    @BeforeEach
    public void init(){
        jwtUtil = mock(JWTUtil.class);
        userDetailsService = mock(UserDetailsService.class);
        ObjectMapper objectMapper = new ObjectMapper();

        user = User.builder().id(UUID.randomUUID()).name("123").login("123").password("123").build();
        userDetails = UserDetailsImpl.builder().user(user).build();

        mvc = MockMvcBuilders.standaloneSetup(new TestController())
                .addFilter(new JWTFilter(jwtUtil, userDetailsService, objectMapper)).build();

        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void testJwtFilterWhenTokenHasBearerButNotCorrect() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer sdfsdf");

        given(jwtUtil.validateTokenAndRetrieveClaim(ArgumentMatchers.anyString())).willThrow(new JWTVerificationException("Token is incorrect"));

        ResultActions resultActions = mvc.perform(
                get("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(headers)
        );

        resultActions.andExpect(status().isUnauthorized());
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
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

        resultActions.andExpect(status().isOk());
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testJwtFilterWhenTokenIsCorrect() throws Exception {
        given(jwtUtil.validateTokenAndRetrieveClaim(ArgumentMatchers.anyString())).willReturn(userDetails.getUsername());
        given(userDetailsService.loadUserByUsername(ArgumentMatchers.anyString())).willReturn(userDetails);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer correcttoken");

        ResultActions resultActions = mvc.perform(
                get("/")
                        .headers(headers)
        );

        resultActions.andExpect(status().isOk());
        Assertions.assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    public void testJwtFilterWhenTokenIsCorrectButUserNotFound() throws Exception {
        given(jwtUtil.validateTokenAndRetrieveClaim(ArgumentMatchers.anyString())).willReturn(userDetails.getUsername());
        given(userDetailsService.loadUserByUsername(ArgumentMatchers.anyString())).willThrow(new UsernameNotFoundException("Username not found"));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer correcttoken");

        ResultActions resultActions = mvc.perform(
                get("/")
                        .headers(headers)
        );

        resultActions.andExpect(status().isUnauthorized());
        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @RestController
    static class TestController {
        @GetMapping("/")
        @ResponseStatus(HttpStatus.OK)
        public String get() {
            return "got it";
        }
    }
}