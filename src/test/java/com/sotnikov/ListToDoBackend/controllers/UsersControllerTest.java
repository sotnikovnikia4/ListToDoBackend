package com.sotnikov.ListToDoBackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotnikov.ListToDoBackend.config.UserDetailsHolder;
import com.sotnikov.ListToDoBackend.dto.UserDTO;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.JWTUtil;
import com.sotnikov.ListToDoBackend.services.UsersService;
import com.sotnikov.ListToDoBackend.util.EditUserValidator;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

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

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){
        User authenticatedUser = User.builder()
                .id(UUID.fromString("02637f02-bbe9-4a5d-9e7e-6378c0c48b1b")).build();
        given(userDetailsHolder.getUserFromSecurityContext()).willReturn(authenticatedUser);
    }

    @Test
    void testEditShouldReturnEditedUser() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .name("user1")
                .login("login")
                .password("password")
                .build();


        given(usersService.update(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));

        ResultActions resultActions = mockMvc.perform(patch("/users/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.login", CoreMatchers.is(userDTO.getLogin())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password", CoreMatchers.is(userDTO.getPassword())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(userDTO.getName())));
    }

    @Test
    void delete() throws Exception {
        doNothing().when(usersService).delete(ArgumentMatchers.any());

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/users/delete")
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    }
}