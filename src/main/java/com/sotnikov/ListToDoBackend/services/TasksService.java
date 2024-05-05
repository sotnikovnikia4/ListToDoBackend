package com.sotnikov.ListToDoBackend.services;

import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.repotitories.TasksRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TasksService {
    private final TasksRepository tasksRepository;

    public List<Task> getTasks(UUID userId){
        return tasksRepository.findAllByUserId(userId);
    }
}
