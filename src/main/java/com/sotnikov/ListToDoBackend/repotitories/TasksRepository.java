package com.sotnikov.ListToDoBackend.repotitories;

import com.sotnikov.ListToDoBackend.models.Task;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface TasksRepository extends MongoRepository<Task, ObjectId> {

    List<Task> findByUserId(UUID userId);

    void deleteAllByUserId(UUID userId);
}
