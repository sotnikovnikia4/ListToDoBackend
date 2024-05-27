package com.sotnikov.ListToDoBackend.controllers;

import com.sotnikov.ListToDoBackend.config.UserDetailsHolder;
import com.sotnikov.ListToDoBackend.dto.CreationTaskDTO;
import com.sotnikov.ListToDoBackend.dto.TaskDTO;
import com.sotnikov.ListToDoBackend.exceptions.TaskException;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import com.sotnikov.ListToDoBackend.services.TasksService;
import com.sotnikov.ListToDoBackend.util.ChangingTaskValidator;
import com.sotnikov.ListToDoBackend.util.ErrorMessageMaker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.OutputKeys;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TasksService tasksService;
    private final ModelMapper modelMapper;

    private final ChangingTaskValidator changingTaskValidator;

    private final UserDetailsHolder userDetailsHolder;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDTO> getTasks(){
        User user = userDetailsHolder.getUserFromSecurityContext();

        return tasksService.getTasks(user.getId()).stream().map(this::convertToTaskDTO).toList();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TaskDTO> getTask(@PathVariable("id") String taskId){
        User currentUser = userDetailsHolder.getUserFromSecurityContext();
        Task task = tasksService.getOne(taskId, currentUser);

        return new ResponseEntity<>(convertToTaskDTO(task), HttpStatus.OK);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TaskDTO> saveTask(@RequestBody @Valid CreationTaskDTO creationTaskDTO, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new TaskException(ErrorMessageMaker.formErrorMessage(bindingResult));
        }

        Task task = tasksService.save(convertToTask(creationTaskDTO), userDetailsHolder.getUserFromSecurityContext());

        return new ResponseEntity<>(convertToTaskDTO(task), HttpStatus.CREATED);
    }

    @PatchMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO editTask(@RequestBody @Valid TaskDTO taskDTO, BindingResult bindingResult){
        Task updatedTask = convertToTask(taskDTO);

        changingTaskValidator.validate(updatedTask, bindingResult);

        if(bindingResult.hasErrors()){
            throw new TaskException(ErrorMessageMaker.formErrorMessage(bindingResult));
        }

        return convertToTaskDTO(tasksService.update(updatedTask, userDetailsHolder.getUserFromSecurityContext()));
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@RequestParam(name = "id") String id){
        tasksService.delete(id, userDetailsHolder.getUserFromSecurityContext());
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
