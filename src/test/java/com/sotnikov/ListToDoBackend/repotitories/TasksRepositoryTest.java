package com.sotnikov.ListToDoBackend.repotitories;

import com.sotnikov.ListToDoBackend.models.Task;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class TasksRepositoryTest {

    @Autowired
    private TasksRepository tasksRepository;

    @Test
    void testFindByUserId() {
        UUID userId = UUID.fromString("f99c3448-041b-4d00-b7df-6173d1058e03");
        Task task1 = Task.builder()
                .name("task1")
                .userId(userId)
                .build();

        Task task2 = Task.builder()
                .name("task2")
                .userId(userId)
                .build();

        Task task3 = Task.builder()
                .name("task3")
                .userId(UUID.fromString("907cd259-a856-4e49-9047-bbdd37e0bf56"))
                .build();

        tasksRepository.saveAll(List.of(task1, task2, task3));

        List<Task> tasksByUserId = tasksRepository.findByUserId(userId);

        Assertions.assertThat(tasksByUserId.size()).isEqualTo(2);
    }

    @Test
    void testDeleteAllByUserIdIfDocumentHasOtherWithNotSameUserId() {
        UUID userId1 = UUID.fromString("05f27597-dd71-4fbf-ba8e-2aebb12f3b9f");
        Task task1 = Task.builder()
                .name("task1")
                .userId(userId1)
                .build();

        Task task2 = Task.builder()
                .name("task2")
                .userId(userId1)
                .build();
        UUID userId2 = UUID.fromString("c8c8280c-a20b-4923-8b60-39c2001a1fa5");
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

        System.out.println(tasksByUserId2);

        Assertions.assertThat(tasksByUserId1.size()).isEqualTo(0);
        Assertions.assertThat(tasksByUserId2.size()).isEqualTo(1);

    }
}