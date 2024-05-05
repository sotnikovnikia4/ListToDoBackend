package com.sotnikov.ListToDoBackend.repotitories;

import com.sotnikov.ListToDoBackend.models.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TasksRepository extends MongoRepository<Task, String> {
}
