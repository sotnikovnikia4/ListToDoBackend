package com.sotnikov.ListToDoBackend.services;

import com.sotnikov.ListToDoBackend.exceptions.TaskNotCreatedException;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.repotitories.TasksRepository;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TasksService {
    private static final int MAX_LEVEL = 5;

    private final TasksRepository tasksRepository;

    public List<Task> getTasks(UUID userId){
        return tasksRepository.findByUserId(userId.toString());
    }

    public void save(Task task){
        enrich(task);
        if(task.getLevel() > MAX_LEVEL)
            throw new TaskNotCreatedException("Достигнут предел вложенности задач");

        tasksRepository.insert(task);
    }

    private void enrich(Task task){
        User user = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        task.setUserId(user.getId().toString());
        task.setLevel(task.getParentTask() == null ? 1 : task.getLevel() + 1);
    }
}
