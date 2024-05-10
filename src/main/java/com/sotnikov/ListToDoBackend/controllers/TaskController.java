package com.sotnikov.ListToDoBackend.controllers;

import com.sotnikov.ListToDoBackend.dto.CreationTaskDTO;
import com.sotnikov.ListToDoBackend.dto.TaskDTO;
import com.sotnikov.ListToDoBackend.exceptions.TaskException;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import com.sotnikov.ListToDoBackend.services.TasksService;
import com.sotnikov.ListToDoBackend.util.ChangingTaskValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TasksService tasksService;
    private final ModelMapper modelMapper;

    private final ChangingTaskValidator changingTaskValidator;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDTO> getTasks(){
        User user = ((UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        return tasksService.getTasks(user.getId()).stream().map(this::convertToTaskDTO).toList();
    }

    @PostMapping("/add")
    public HttpStatus saveTask(@RequestBody @Valid CreationTaskDTO creationTaskDTO, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new TaskException("Task is not created");
        }

        Task task = convertToTask(creationTaskDTO);

        tasksService.save(task);

        return HttpStatus.CREATED;
    }

    @PatchMapping("/edit")
    public void editTask(@RequestBody @Valid TaskDTO taskDTO, BindingResult bindingResult){
        Task updatedTask = convertToTask(taskDTO);

        changingTaskValidator.validate(updatedTask, bindingResult);

        if(bindingResult.hasErrors()){
            throw new TaskException("Data of task is not changed");
        }

        tasksService.update(updatedTask);
    }

    @DeleteMapping("/delete")
    public void deleteTask(@RequestParam(name = "id") String id){
        tasksService.delete(id);
    }

    private Task convertToTask(CreationTaskDTO creationTaskDTO){
        return modelMapper.map(creationTaskDTO, Task.class);
    }

    private Task convertToTask(TaskDTO taskDTO){
        return modelMapper.map(taskDTO, Task.class);
    }

    private TaskDTO convertToTaskDTO(Task task){
        return modelMapper.map(task, TaskDTO.class);
    }
}
