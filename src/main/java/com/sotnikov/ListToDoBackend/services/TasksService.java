package com.sotnikov.ListToDoBackend.services;

import com.sotnikov.ListToDoBackend.exceptions.TaskException;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.repotitories.TasksRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
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

    public Task getOne(String id, User user){
        Optional<Task> task = tasksRepository.findById(convertToObjectId(id));

        if(task.isEmpty()){
            throw new TaskException("This task does not exist!");
        }
        else if(!task.get().getUserId().equals(user.getId())){
            throw new TaskException("The task does not belong to this user!");
        }

        return task.get();
    }

    public Task save(Task task, User user){
        enrich(task, user);

        return tasksRepository.insert(task);
    }

    public Task update(String taskToBeUpdatedId, Task updatedTask, User user){
        Task taskToBeUpdated = getOne(taskToBeUpdatedId, user);

        taskToBeUpdated.setCompleted(updatedTask.isCompleted());
        taskToBeUpdated.setPriority(updatedTask.getPriority());
        taskToBeUpdated.setSubtasks(updatedTask.getSubtasks());
        taskToBeUpdated.setDeadline(updatedTask.getDeadline());
        taskToBeUpdated.setDescription(updatedTask.getDescription());
        taskToBeUpdated.setTag(updatedTask.getTag());
        taskToBeUpdated.setName(updatedTask.getName());

        return tasksRepository.save(taskToBeUpdated);
    }

    public void delete(String taskId, User user){
        Task taskWithSameId = getOne(taskId, user);

        tasksRepository.delete(taskWithSameId);
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
