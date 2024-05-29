package com.sotnikov.ListToDoBackend.services;

import com.sotnikov.ListToDoBackend.exceptions.TaskException;
import com.sotnikov.ListToDoBackend.models.Subtask;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.repotitories.TasksRepository;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TasksServiceTest {
    @Mock
    private TasksRepository tasksRepository;

    @InjectMocks
    private TasksService tasksService;

    private static User authenticatedUser;

    private Task task;

    @BeforeAll
    static void setUpBeforeAll(){
        authenticatedUser = User.builder()
                .id(UUID.fromString("a722ec21-86dc-4130-8027-c96c95c2d799"))
                .name("John")
                .login("123")
                .password("123")
                .build();
    }

    @BeforeEach
    public void setUp(){
        String taskId = ObjectId.get().toHexString();

        task = Task.builder()
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
        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(Task.builder().id(task.getId()).userId(task.getUserId()).build()));
        when(tasksRepository.save(Mockito.any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task updatedTask = tasksService.update(task.getId(), task, authenticatedUser);

        Assertions.assertThat(updatedTask).isEqualTo(task);
    }

    @Test
    void testUpdateWithNotAuthenticatedUser_ShouldThrowTaskException() {
        task.setUserId(UUID.fromString("81b3d0dc-54c4-4cbe-b285-062fc7f0f5e4"));

        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(Task.builder().id(task.getId()).userId(task.getUserId()).build()));

        TaskException e = assertThrows(TaskException.class, () -> tasksService.update(task.getId(), task, authenticatedUser));
        assertEquals("The task does not belong to this user!", e.getMessage());
    }

    @Test
    void testUpdateWhenTaskDoesNotExist_ShouldThrowTaskException() {
        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.empty());

        TaskException e = assertThrows(TaskException.class, () -> tasksService.update(task.getId(), task, authenticatedUser));
        assertEquals("This task does not exist!", e.getMessage());
    }

    @Test
    void testDeleteWhenTaskExist_NotShouldThrowException() {
        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(task));
        doNothing().when(tasksRepository).delete(ArgumentMatchers.any());

        assertDoesNotThrow(() -> tasksService.delete(task.getId(), authenticatedUser));
    }

    @Test
    void testDeleteWhenTaskDoesNotExist_ShouldThrowException() {
        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.empty());

        assertThrows(TaskException.class, () -> tasksService.delete(task.getId(), authenticatedUser));
    }
}