package com.sotnikov.ListToDoBackend.services;

import com.sotnikov.ListToDoBackend.config.UserDetailsHolder;
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
public class TasksService {

    private final TasksRepository tasksRepository;

    public List<Task> getTasks(UUID userId){
        return tasksRepository.findByUserId(userId);
    }

    public Optional<Task> getOne(String id){
        return tasksRepository.findById(convertToObjectId(id));
    }

    public Task save(Task task, User user){
        enrich(task, user);

        return tasksRepository.insert(task);
    }

    public Task update(Task updatedTask){
        Optional<Task> taskWithSameId = getOne(updatedTask.getId());

        if(taskWithSameId.isPresent()){
            taskWithSameId.get().setCompleted(updatedTask.isCompleted());
            taskWithSameId.get().setPriority(updatedTask.getPriority());
            taskWithSameId.get().setSubtasks(updatedTask.getSubtasks());
            taskWithSameId.get().setDeadline(updatedTask.getDeadline());
            taskWithSameId.get().setDescription(updatedTask.getDescription());
            taskWithSameId.get().setTag(updatedTask.getTag());
            taskWithSameId.get().setName(updatedTask.getName());

            return tasksRepository.save(taskWithSameId.get());
        }
        else{
            throw new TaskException("The task does not exist");
        }
    }

    public void delete(String id){
        Optional<Task> taskWithSameId = getOne(id);

        if(taskWithSameId.isPresent()){
            tasksRepository.delete(taskWithSameId.get());
        }
        else{
            throw new TaskException("The task does not exist");
        }
    }

    private void enrich(Task task, User user){

        task.setUserId(user.getId());
    }

    private ObjectId convertToObjectId(String id){
        try{
            return new ObjectId(id);
        }
        catch(IllegalArgumentException e){
            throw new TaskException("A task with the id does not exist");
        }
    }
}
