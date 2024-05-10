package com.sotnikov.ListToDoBackend.services;

import com.sotnikov.ListToDoBackend.exceptions.TaskException;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.repotitories.TasksRepository;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TasksService {//TODO
    private static final int MAX_LEVEL = 5;

    private final TasksRepository tasksRepository;

    public List<Task> getTasks(UUID userId){
        return tasksRepository.findByUserId(userId.toString());
    }

    public Optional<Task> getOne(String id){
        return tasksRepository.findById(new ObjectId(id));
    }

    public void save(Task task){
        enrich(task);

        tasksRepository.insert(task);
    }

    public void update(Task updatedTask){
        Optional<Task> taskToUpdate = (tasksRepository.findById(new ObjectId(updatedTask.getId())));

        if(taskToUpdate.isPresent()){
            taskToUpdate.get().setName(updatedTask.getName());
            taskToUpdate.get().setDeadline(updatedTask.getDeadline());
            taskToUpdate.get().setCompleted(updatedTask.isCompleted());
            taskToUpdate.get().setDescription(updatedTask.getDescription());
            taskToUpdate.get().setPriority(updatedTask.getPriority());
            taskToUpdate.get().setSubtasks(updatedTask.getSubtasks());
        }
        else{
            throw new TaskException("The task does not exist");
        }

    }

    private void enrich(Task task){
        User user = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        task.setUserId(user.getId().toString());
    }
}
