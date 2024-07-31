package com.sotnikov.ListToDoBackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotnikov.ListToDoBackend.dto.CreationTaskDTO;
import com.sotnikov.ListToDoBackend.dto.FilterTask;
import com.sotnikov.ListToDoBackend.dto.TaskDTO;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.JWTUtil;
import com.sotnikov.ListToDoBackend.security.UserDetailsHolder;
import com.sotnikov.ListToDoBackend.services.TasksService;
import com.sotnikov.ListToDoBackend.util.TaskValidator;
import org.bson.types.ObjectId;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureDataMongo
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsHolder userDetailsHolder;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JWTUtil jwtUtil;

    @MockBean
    private TasksService tasksService;

    @MockBean
    private TaskValidator taskValidator;

    private TaskDTO taskDTO;

    private static User authenticatedUser;
    private static List<Task> tasks;

    @BeforeAll
    static void setUpBeforeAll(){
        authenticatedUser = User.builder()
                .id(UUID.fromString("a722ec21-86dc-4130-8027-c96c95c2d799"))
                .name("John")
                .login("123")
                .password("123")
                .build();

        Task task1 = Task.builder()
                .name("task1")
                .userId(authenticatedUser.getId())
                .build();

        Task task2 = Task.builder()
                .name("task2")
                .userId(authenticatedUser.getId())
                .build();
        Task task3 = Task.builder()
                .name("task3")
                .userId(authenticatedUser.getId())
                .completed(true)
                .priority(1)
                .build();

        tasks = List.of(task1, task2, task3);
    }

    @BeforeEach
    void setUp(){
        given(userDetailsHolder.getUserFromSecurityContext()).willReturn(authenticatedUser);

        taskDTO = TaskDTO.builder()
                .id(ObjectId.get().toHexString())
                .name("task3")
                .priority(1)
                .description("description")
                .deadline(LocalDateTime.now())
                .subtasks(Collections.emptyList())
                .build();
    }

    @Test
    public void testGetAllTasks_ReturnsList() throws Exception {

        given(tasksService.getTasks(ArgumentMatchers.any())).willReturn(tasks);

        ResultActions resultActions = mockMvc.perform(get("/tasks/get-all"));

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", CoreMatchers.is(tasks.size())));
    }

    @Test
    public void testGetOneTask_ReturnsTaskDTO() throws Exception {
        Task task1 = Task.builder()
                .id(taskDTO.getId())
                .name(taskDTO.getName())
                .userId(authenticatedUser.getId())
                .completed(taskDTO.isCompleted())
                .priority(taskDTO.getPriority())
                .description(taskDTO.getDescription())
                .deadline(taskDTO.getDeadline())
                .subtasks(Collections.emptyList())
                .build();

        given(tasksService.getOne(task1.getId(), authenticatedUser)).willReturn(task1);

        ResultActions resultActions = mockMvc.perform(get("/tasks/" + task1.getId()));

        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(taskDTO)));
    }

    @Test
    public void testSaveTask_ReturnsSavedTask() throws Exception {
        CreationTaskDTO creationTaskDTO = CreationTaskDTO.builder()
                .name("task3")
                .priority(1)
                .description("description")
                .deadline(LocalDateTime.now())
                .subtasks(Collections.emptyList())
                .build();

        given(tasksService.save(ArgumentMatchers.any(), ArgumentMatchers.any())).willAnswer(invocationOnMock -> {
            Task t = invocationOnMock.getArgument(0);

            t.setId(taskDTO.getId());

            return t;
        });

        ResultActions resultActions = mockMvc.perform(post("/tasks/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creationTaskDTO)));

        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(taskDTO.getId()));
    }

    @Test
    public void testUpdateTask_ReturnsUpdatedTask() throws Exception {
        given(tasksService.update(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).willAnswer(invocationOnMock -> invocationOnMock.getArgument(1));

        ResultActions resultActions = mockMvc.perform(patch("/tasks/" + taskDTO.getId() + "/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTO)));

        resultActions.andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(taskDTO)));
    }

    @Test
    public void testEditTask_ReturnsNothing() throws Exception {
        doNothing().when(tasksService).delete(taskDTO.getId(), authenticatedUser);

        ResultActions resultActions = mockMvc.perform(delete("/tasks/" + taskDTO.getId() + "/delete")
                .contentType(MediaType.APPLICATION_JSON));

        resultActions.andExpect(status().isOk());
    }

    @Test
    public void testSetCompleted_ReturnsNothing() throws Exception {
        doNothing().when(tasksService).setCompleted(taskDTO.getId(), true, authenticatedUser);

        ResultActions resultActions = mockMvc.perform(
                put("/tasks/" + taskDTO.getId() + "/set-completed")
                .param("completed", "true")
        );

        resultActions.andExpect(status().isOk());
    }

    @Test
    public void testGetTasksWithCriteria_ReturnsListTaskDTO() throws Exception{
        FilterTask filterTask = FilterTask.builder().field("field").operator("operator").value("value").build();

        given(tasksService.getTasks(authenticatedUser.getId(), List.of(filterTask))).willAnswer(e -> tasks);

        ResultActions resultActions = mockMvc.perform(
                get("/tasks/get-all-with-criteria")
                        .content(objectMapper.writeValueAsString(List.of(filterTask)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions.andExpect(status().isOk());
    }
}




















