package com.sotnikov.ListToDoBackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotnikov.ListToDoBackend.dto.CreationSubtaskDTO;
import com.sotnikov.ListToDoBackend.dto.CreationTaskDTO;
import com.sotnikov.ListToDoBackend.dto.TaskDTO;
import com.sotnikov.ListToDoBackend.exceptions.TaskException;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import com.sotnikov.ListToDoBackend.services.TasksService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {
    @Mock
    private TasksService tasksService;

    private ModelMapper modelMapper;
    @InjectMocks
    private TaskController taskController;

    private User user;
    private static ObjectMapper objectMapper;


    private MockMvc mockMvc;

    @BeforeAll
    static void setUpBeforeAll(){
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @BeforeEach
    void setUp() {
        try{
            setUpAuthentication();
            setUpTaskController();


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

    @Test
    void testGetTasks() throws Exception {
        Task task1 = new Task("task1", "name1", null, LocalDateTime.now(), null, 1, false, Collections.emptyList(), user.getId());
        Task task2 = new Task("task2", "name2", "sdfsdf", LocalDateTime.now(), null, 10, true, Collections.emptyList(), UUID.randomUUID());

        List<Task> tasks = List.of(task1, task2);
        List<TaskDTO> taskDTOS = tasks.stream().map(t -> modelMapper.map(t, TaskDTO.class)).toList();

        when(tasksService.getTasks(user.getId())).thenReturn(tasks);

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(taskDTOS)));
    }

    @Test
    void testSaveTaskIfSuccessful() throws Exception {
        CreationTaskDTO creationTaskDTO = new CreationTaskDTO(
                "task1",
                "some description",
                LocalDateTime.now().plusYears(1),
                null,
                0,
                List.of(
                        new CreationSubtaskDTO("subtask1", null),
                        new CreationSubtaskDTO("subtask2", "subtask description")
                )
        );

        ArgumentCaptor<Task> valueCapture = ArgumentCaptor.forClass(Task.class);
        doAnswer(invocationOnMock -> {
            Task t = invocationOnMock.getArgument(0);
            t.setUserId(user.getId());
            t.setId(new ObjectId().toString());
            return null;
        }).when(tasksService).save(valueCapture.capture());

        mockMvc.perform(
                post("/tasks/add")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(creationTaskDTO))
                        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testSaveTaskIfNotSuccessfulThrowTaskException() throws Exception {
        CreationTaskDTO creationTaskDTO = new CreationTaskDTO(
                "",
                "some description",
                null,
                null,
                11,
                null
        );
        mockMvc.perform(
                post("/tasks/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creationTaskDTO))
        );


    }
}




















