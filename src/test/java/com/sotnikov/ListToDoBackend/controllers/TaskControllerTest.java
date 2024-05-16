package com.sotnikov.ListToDoBackend.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sotnikov.ListToDoBackend.dto.TaskDTO;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import com.sotnikov.ListToDoBackend.services.TasksService;
import com.sotnikov.ListToDoBackend.util.ChangingTaskValidator;
import org.junit.jupiter.api.Assertions;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TasksService tasksService;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ChangingTaskValidator changingTaskValidator;
    @InjectMocks
    private TaskController taskController;

    private User user;


    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        user = new User(UUID.randomUUID(), "login", "password", "name", LocalDateTime.now());
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new UserDetailsImpl(user), "password", Collections.emptyList()
        );

        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    @Test
    void getTasks() throws Exception {
        Task task1 = new Task("task1", "name1", null, LocalDateTime.now(), null, 1, false, Collections.emptyList(), user.getId());
        Task task2 = new Task("task2", "name2", "sdfsdf", LocalDateTime.now(), null, 10, true, Collections.emptyList(), UUID.randomUUID());

        List<Task> tasks = List.of(task1, task2);
        List<TaskDTO> taskDTOS =

        Mockito.when(tasksService.getTasks(user.getId())).thenReturn(tasks);

        AtomicReference<String> contentResult = new AtomicReference<>("");

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    contentResult.set(result.getResponse().getContentAsString());
                });

        Assertions.assertEquals("", contentResult.get());
    }
}




















