package com.sotnikov.ListToDoBackend.controllers;

import ch.qos.logback.core.boolex.EvaluationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotnikov.ListToDoBackend.config.UserDetailsHolder;
import com.sotnikov.ListToDoBackend.security.JWTUtil;
import com.sotnikov.ListToDoBackend.services.UsersService;
import com.sotnikov.ListToDoBackend.util.EditUserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = UsersController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@AutoConfigureDataMongo
class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTUtil jwtUtil;

    @MockBean
    private EditUserValidator editUserValidator;

    //@MockBean
    //private ModelMapper modelMapper;

    @MockBean
    private UsersService usersService;

    @MockBean
    private UserDetailsHolder userDetailsHolder;

    @Test
    void edit() {
        
    }

    @Test
    void delete() {
    }
}