package com.sotnikov.ListToDoBackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sotnikov.ListToDoBackend.config.JWTFilter;
import com.sotnikov.ListToDoBackend.dto.CreationSubtaskDTO;
import com.sotnikov.ListToDoBackend.dto.CreationTaskDTO;
import com.sotnikov.ListToDoBackend.dto.TaskDTO;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.JWTUtil;
import com.sotnikov.ListToDoBackend.services.TasksService;
import com.sotnikov.ListToDoBackend.util.ChangingTaskValidator;
import com.sotnikov.ListToDoBackend.util.EditUserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@AutoConfigureDataMongo
class TaskControllerTest {

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private TasksService tasksService;

    @MockBean
    private ChangingTaskValidator changingTaskValidator;

    @MockBean
    private JWTFilter jwtFilter;

    private User user;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;



    @BeforeEach
    void setUp() {
        try{
            setUpUser();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    void setUpUser(){
        user = User.builder()
                .id(UUID.randomUUID())
                .login("login")
                .password("password")
                .name("name")
                .registeredAt(LocalDateTime.now()).build();
    }

    @Test
    void testGetTasks() throws Exception {
        Task task1 = new Task("task1", "name1", null, LocalDateTime.now(), null, 1, false, Collections.emptyList(), user.getId());
        Task task2 = new Task("task2", "name2", "sdfsdf", LocalDateTime.now(), null, 10, true, Collections.emptyList(), UUID.randomUUID());

        List<Task> tasks = List.of(task1, task2);
        List<TaskDTO> taskDTOS = tasks.stream().map(t -> modelMapper.map(t, TaskDTO.class)).toList();

        given(tasksService.getTasks(ArgumentMatchers.any())).will(invocation -> invocation.getArgument(0));

        ResultActions result = mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskDTOS)));
    }

    @Test
    void testSaveTaskIfSuccessful() throws Exception {
        CreationTaskDTO creationTaskDTO = CreationTaskDTO.builder()
                .name("task1")
                .description("some description")
                .deadline(LocalDateTime.now().plusYears(1))
                .priority(0)
                .subtasks(
                        List.of(
                                CreationSubtaskDTO.builder().name("subtask1").build(),
                                CreationSubtaskDTO.builder().name("subtask2").description("subtask description").build()
                        )
                ).build();

//        ArgumentCaptor<Task> valueCapture = ArgumentCaptor.forClass(Task.class);
//        doAnswer(invocationOnMock -> {
//            Task t = invocationOnMock.getArgument(0);
//            t.setUserId(user.getId());
//            t.setId(new ObjectId().toString());
//            return null;
//        }).when(tasksService).save(valueCapture.capture());
       // given(tasksService.save(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArguments());


    }
}




















