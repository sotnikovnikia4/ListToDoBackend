package com.sotnikov.ListToDoBackend.controllers;

import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.repotitories.TasksRepository;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import com.sotnikov.ListToDoBackend.services.TasksService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TasksService tasksService;

    @GetMapping
    public List<Task> getTasks(){
        User user = ((UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        return tasksService.getTasks(user.getId());
    }

    @PostMapping("/add")
    public HttpStatus save(){
        Task task = new Task();

        task.setDescription("some description");
        task.setName("name");
        task.setLevel(1);
        task.setUserId("06770fef-efc2-4280-a644-c18d8b8b0dba");

        tasksService.save(task);

        return HttpStatus.CREATED;
    }
}
