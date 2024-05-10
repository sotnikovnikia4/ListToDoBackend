package com.sotnikov.ListToDoBackend.controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.sotnikov.ListToDoBackend.dto.CreationTaskDTO;
import com.sotnikov.ListToDoBackend.exceptions.TaskNotCreatedException;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.repotitories.TasksRepository;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import com.sotnikov.ListToDoBackend.services.TasksService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TasksService tasksService;
    private final ModelMapper modelMapper;

    @GetMapping
    public List<Task> getTasks(){
        User user = ((UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        return tasksService.getTasks(user.getId());
    }

    @PostMapping("/add")
    public HttpStatus saveTask(@RequestBody @Valid CreationTaskDTO creationTaskDTO, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new TaskNotCreatedException("Task is not created");
        }

        Task task = convertToTask(creationTaskDTO);

        tasksService.save(task);

        return HttpStatus.CREATED;
    }

    private Task convertToTask(CreationTaskDTO creationTaskDTO){
        return modelMapper.map(creationTaskDTO, Task.class);
    }
}
