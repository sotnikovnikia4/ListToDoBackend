package com.sotnikov.ListToDoBackend.services;

import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.repotitories.TasksRepository;
import org.assertj.core.api.Assertions;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TasksServiceTest {
    @Mock
    private TasksRepository tasksRepository;

    @InjectMocks
    private TasksService tasksService;

    @Test
    void getTasks_ReturnsNotNull() {
        when(tasksRepository.findByUserId(Mockito.any(UUID.class))).thenReturn(List.of(new Task()));

        List<Task> tasks = tasksService.getTasks(UUID.randomUUID());

        Assertions.assertThat(tasks.size()).isEqualTo(1);
    }

    @Test
    void getOne() {
        when(tasksRepository.findById(Mockito.any(ObjectId.class))).thenReturn(Optional.of(new Task()));

        Task task = tasksService.getOne(ObjectId.get().toHexString()).get();

        Assertions.assertThat(task).isNotNull();
    }

    @Test
    void save() {
        when(tasksRepository.insert(Mockito.any(Task.class))).thenReturn(new Task());

        Task task = Task.builder()
                .name("task1")
                .build();
        //tasksService.save(task);

        Assertions.assertThat(task).isNotNull();

    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}