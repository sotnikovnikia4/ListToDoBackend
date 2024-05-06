package com.sotnikov.ListToDoBackend.repotitories;

import com.sotnikov.ListToDoBackend.models.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TasksRepository extends MongoRepository<Task, String> {
    //@Query("find({userId: '?0'})")
    List<Task> findByUserId(String userId);
}
