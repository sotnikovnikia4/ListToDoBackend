package com.sotnikov.ListToDoBackend.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotnikov.ListToDoBackend.dto.TaskDTO;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import com.sotnikov.ListToDoBackend.services.TasksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TasksService tasksService;

    private ModelMapper modelMapper;
    @InjectMocks
    private TaskController taskController;

    private User user;
    private ObjectMapper objectMapper;


    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        try{
            setUpAuthentication();
            setUpTaskController();
            setUpObjectMapper();

            mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    void setUpTaskController() throws NoSuchFieldException, IllegalAccessException {
        modelMapper = new ModelMapper();

        modelMapper = new ModelMapper();

        Field modelMapperField = taskController.getClass().getDeclaredField("modelMapper");
        modelMapperField.setAccessible(true);
        modelMapperField.set(taskController, modelMapper);
    }

    void setUpAuthentication(){
        user = new User(UUID.randomUUID(), "login", "password", "name", LocalDateTime.now());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new UserDetailsImpl(user), "password", Collections.emptyList()
        );

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
    void setUpObjectMapper(){
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void getTasks() throws Exception {
        Task task1 = new Task("task1", "name1", null, LocalDateTime.now(), null, 1, false, Collections.emptyList(), user.getId());
        Task task2 = new Task("task2", "name2", "sdfsdf", LocalDateTime.now(), null, 10, true, Collections.emptyList(), UUID.randomUUID());

        List<Task> tasks = List.of(task1, task2);
        List<TaskDTO> taskDTOS = tasks.stream().map(t -> modelMapper.map(t, TaskDTO.class)).toList();

        Mockito.when(tasksService.getTasks(user.getId())).thenReturn(tasks);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(taskDTOS)));
    }
}




















