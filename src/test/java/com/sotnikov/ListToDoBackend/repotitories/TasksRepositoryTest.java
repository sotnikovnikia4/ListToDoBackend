package com.sotnikov.ListToDoBackend.repotitories;

import com.sotnikov.ListToDoBackend.models.QTask;
import com.sotnikov.ListToDoBackend.models.Task;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class TasksRepositoryTest {

    @Autowired
    private TasksRepository tasksRepository;

    private static List<Task> tasks;
    private static UUID userId1, userId2;

    @BeforeAll
    static void setUpAll(){
        userId1 = UUID.fromString("05f27597-dd71-4fbf-ba8e-2aebb12f3b9f");
        Task task1 = Task.builder()
                .name("task1")
                .userId(userId1)
                .build();
        Task task2 = Task.builder()
                .name("task2")
                .userId(userId1)
                .build();

        userId2 = UUID.fromString("c8c8280c-a20b-4923-8b60-39c2001a1fa5");
        Task task3 = Task.builder()
                .name("task3")
                .userId(userId2)
                .completed(true)
                .priority(1)
                .build();

        tasks = List.of(task1, task3, task2);
    }

    @BeforeEach
    void setUp(){
        tasksRepository.saveAll(tasks);
    }

    @Test
    void testFindByUserId() {
        List<Task> tasksByUserId = tasksRepository.findByUserId(userId1);

        assertThat(tasksByUserId.size()).isEqualTo(2);
    }

    @Test
    void testDeleteAllByUserIdIfDocumentHasOtherWithNotSameUserId() {
        tasksRepository.deleteAllByUserId(userId1);

        List<Task> tasksByUserId1 = tasksRepository.findByUserId(userId1);
        List<Task> tasksByUserId2 = tasksRepository.findByUserId(userId2);

        assertThat(tasksByUserId1.size()).isEqualTo(0);
        assertThat(tasksByUserId2.size()).isEqualTo(1);
    }

    @Test
    void testFindAllWithPredicateAndSort_ShouldReturnSortedList() {
        List<Task> result = new ArrayList<>();
        tasksRepository.findAll(QTask.task.name.containsIgnoreCase("task"), Sort.by("name")).forEach(result::add);

        List<Task> expected = new ArrayList<>(result);
        expected.sort(Comparator.comparing(Task::getName));

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void testFindAllWithPredicateAndPageable_ShouldReturnPage(){
        Pageable pageable = PageRequest.of(0, 2);
        Page<Task> page = tasksRepository.findAll(QTask.task.name.containsIgnoreCase("task"), pageable);

        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(2);
        assertThat(page.getContent().size()).isEqualTo(2);
    }

    @Test
    void testFindAllWithPredicateAndPageable_ShouldReturnSortedPage(){
        Pageable pageable = PageRequest.of(0, 2, Sort.by("name"));
        Page<Task> page = tasksRepository.findAll(QTask.task.name.containsIgnoreCase("task"), pageable);

        List<Task> expected = new ArrayList<>(page.getContent());
        expected.sort(Comparator.comparing(Task::getName));

        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(2);
        assertThat(page.getContent()).isEqualTo(expected);
    }
}