package com.sotnikov.ListToDoBackend.repotitories;

import com.sotnikov.ListToDoBackend.models.Task;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

@DataMongoTest
class TasksRepositoryTest {

    @Autowired
    private TasksRepository tasksRepository;

    @Test
    void testFindByUserId() {
        UUID userId = UUID.randomUUID();
        Task task1 = Task.builder()
                .name("task1")
                .userId(userId)
                .build();

        Task task2 = Task.builder()
                .name("task2")
                .userId(userId)
                .build();

        tasksRepository.saveAll(List.of(task1, task2));

        List<Task> tasksByUserId = tasksRepository.findByUserId(userId);

        Assertions.assertThat(tasksByUserId.size()).isEqualTo(2);
    }

    @Test
    void testDeleteAllByUserIdIfDocumentHasOtherWithNotSameUserId() {
        UUID userId1 = UUID.randomUUID();
        Task task1 = Task.builder()
                .name("task1")
                .userId(userId1)
                .build();

        Task task2 = Task.builder()
                .name("task2")
                .userId(userId1)
                .build();
        UUID userId2 = UUID.randomUUID();
        Task task3 = Task.builder()
                .name("task3")
                .userId(userId2)
                .completed(true)
                .priority(1)
                .build();

        tasksRepository.saveAll(List.of(task1, task2, task3));
        tasksRepository.deleteAllByUserId(userId1);

        List<Task> tasksByUserId1 = tasksRepository.findByUserId(userId1);
        List<Task> tasksByUserId2 = tasksRepository.findByUserId(userId2);

        Assertions.assertThat(tasksByUserId1.size()).isEqualTo(0);
        Assertions.assertThat(tasksByUserId2.size()).isEqualTo(1);
    }
}