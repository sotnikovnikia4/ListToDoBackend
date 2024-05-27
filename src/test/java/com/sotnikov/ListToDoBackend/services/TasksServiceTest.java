package com.sotnikov.ListToDoBackend.services;

import com.sotnikov.ListToDoBackend.exceptions.TaskException;
import com.sotnikov.ListToDoBackend.models.Subtask;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.repotitories.TasksRepository;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TasksServiceTest {
    @Mock
    private TasksRepository tasksRepository;

    @InjectMocks
    private TasksService tasksService;

    private static User authenticatedUser;

    @BeforeAll
    static void setUpBeforeAll(){
        authenticatedUser = User.builder()
                .id(UUID.fromString("a722ec21-86dc-4130-8027-c96c95c2d799"))
                .name("John")
                .login("123")
                .password("123")
                .build();
    }

    @Test
    void testGetTasks_ShouldReturnNotNull() {
        when(tasksRepository.findByUserId(Mockito.any(UUID.class))).thenReturn(List.of(new Task()));

        List<Task> tasks = tasksService.getTasks(UUID.randomUUID());

        Assertions.assertThat(tasks.size()).isEqualTo(1);
    }

    @Test
    void testGetOneWithAuthenticatedUser_ShouldReturnNotNull() {
        Task task = Task.builder().userId(authenticatedUser.getId()).build();

        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(task));

        Task foundTask = tasksService.getOne(ObjectId.get().toHexString(), authenticatedUser);

        Assertions.assertThat(foundTask).isNotNull();
    }

    @Test
    void testGetOneWithNotAuthenticatedUser_ShouldThrowTaskException() {
        Task task = Task.builder().userId(UUID.fromString("81b3d0dc-54c4-4cbe-b285-062fc7f0f5e4")).build();

        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(task));

        TaskException e = assertThrows(TaskException.class, () -> tasksService.getOne(ObjectId.get().toHexString(), authenticatedUser));
        assertEquals("The task does not belong to this user!", e.getMessage());
    }

    @Test
    void testGetOneWhenTaskDoesNotExist_ShouldThrowTaskException() {
        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.empty());

        TaskException e = assertThrows(TaskException.class, () -> tasksService.getOne(ObjectId.get().toHexString(), authenticatedUser));
        assertEquals("This task does not exist!", e.getMessage());
    }

    @Test
    void testSave_ShouldReturnNotNull() {

        when(tasksRepository.insert(Mockito.any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));


        Task task = Task.builder()
                .name("task")
                .build();
        task = tasksService.save(task, authenticatedUser);

        Assertions.assertThat(task).isNotNull();
        Assertions.assertThat(task.getUserId()).isEqualTo(authenticatedUser.getId());
    }

    @Test
    void testUpdateWithAuthenticatedUser_ShouldReturnUpdatedTask() {
        String taskId = ObjectId.get().toHexString();

        Task task = Task.builder()
                .id(taskId)
                .description("description")
                .completed(true)
                .priority(1)
                .name("name")
                .userId(authenticatedUser.getId())
                .deadline(LocalDateTime.now())
                .subtasks(List.of(Subtask.builder().name("subtask").build()))
                .tag("tag")
                .build();

        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(Task.builder().userId(task.getUserId()).build()));
        when(tasksRepository.save(Mockito.any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            t.setId(taskId);
            return t;
        });

        Task updatedTask = tasksService.update(task, authenticatedUser);

        Assertions.assertThat(updatedTask).isEqualTo(task);
    }

    @Test
    void testUpdateWithNotAuthenticatedUser_ShouldThrowTaskException() {
        String taskId = ObjectId.get().toHexString();

        Task task = Task.builder()
                .id(taskId)
                .description("description")
                .completed(true)
                .priority(1)
                .name("name")
                .userId(UUID.fromString("81b3d0dc-54c4-4cbe-b285-062fc7f0f5e4"))
                .deadline(LocalDateTime.now())
                .subtasks(List.of(Subtask.builder().name("subtask").build()))
                .tag("tag")
                .build();

        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(Task.builder().id(taskId).userId(task.getUserId()).build()));

        TaskException e = assertThrows(TaskException.class, () -> tasksService.update(task, authenticatedUser));
        assertEquals("The task does not belong to this user!", e.getMessage());
    }

    @Test
    void testUpdateWhenTaskDoesNotExist_ShouldThrowTaskException() {
        String taskId = ObjectId.get().toHexString();

        Task task = Task.builder()
                .id(taskId)
                .description("description")
                .completed(true)
                .priority(1)
                .name("name")
                .userId(UUID.fromString("81b3d0dc-54c4-4cbe-b285-062fc7f0f5e4"))
                .deadline(LocalDateTime.now())
                .subtasks(List.of(Subtask.builder().name("subtask").build()))
                .tag("tag")
                .build();

        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.empty());

        TaskException e = assertThrows(TaskException.class, () -> tasksService.update(task, authenticatedUser));
        assertEquals("This task does not exist!", e.getMessage());
    }



    @Test
    void testDeleteWhenTaskExist_NotShouldThrowException() {
        String taskId = ObjectId.get().toHexString();
        Task task = Task.builder()
                .id(taskId)
                .description("description")
                .completed(true)
                .priority(1)
                .name("name")
                .userId(authenticatedUser.getId())
                .deadline(LocalDateTime.now())
                .subtasks(List.of(Subtask.builder().name("subtask").build()))
                .tag("tag")
                .build();

        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(task));
        doNothing().when(tasksRepository).delete(ArgumentMatchers.any());

        assertDoesNotThrow(() -> tasksService.delete(task.getId(), authenticatedUser));

    }

    @Test
    void testDeleteWhenTaskDoesNotExist_NotShouldThrowException() {
        String taskId = ObjectId.get().toHexString();
        Task task = Task.builder()
                .id(taskId)
                .description("description")
                .completed(true)
                .priority(1)
                .name("name")
                .userId(UUID.fromString("81b3d0dc-54c4-4cbe-b285-062fc7f0f5e4"))
                .deadline(LocalDateTime.now())
                .subtasks(List.of(Subtask.builder().name("subtask").build()))
                .tag("tag")
                .build();

        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.empty());

        assertThrows(TaskException.class, () -> tasksService.delete(task.getId(), authenticatedUser));
    }
}