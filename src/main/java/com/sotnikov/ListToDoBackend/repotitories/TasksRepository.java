package com.sotnikov.ListToDoBackend.repotitories;

import com.sotnikov.ListToDoBackend.models.Task;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.UUID;

public interface TasksRepository extends MongoRepository<Task, ObjectId>, QuerydslPredicateExecutor<Task> {

    List<Task> findByUserId(UUID userId);

    void deleteAllByUserId(UUID userId);
}
