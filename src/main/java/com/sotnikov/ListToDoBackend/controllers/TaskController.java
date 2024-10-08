package com.sotnikov.ListToDoBackend.controllers;

import com.sotnikov.ListToDoBackend.dto.CreationTaskDTO;
import com.sotnikov.ListToDoBackend.dto.FilterTask;
import com.sotnikov.ListToDoBackend.dto.PageDTO;
import com.sotnikov.ListToDoBackend.dto.TaskDTO;
import com.sotnikov.ListToDoBackend.exceptions.TaskException;
import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.UserDetailsHolder;
import com.sotnikov.ListToDoBackend.services.TasksService;
import com.sotnikov.ListToDoBackend.util.ErrorMessageMaker;
import com.sotnikov.ListToDoBackend.util.TaskValidator;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@SecurityScheme(type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", name = "bearerAuth")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TasksService tasksService;
    private final ModelMapper modelMapper;

    private final TaskValidator taskValidator;

    private final UserDetailsHolder userDetailsHolder;

    @GetMapping("/get-all")
    @ResponseStatus(HttpStatus.OK)
    public List<TaskDTO> getAllTasks(@Valid @RequestBody(required = false) List<FilterTask> filters, BindingResult bindingResult){
        User user = userDetailsHolder.getUserFromSecurityContext();

        if(filters == null){
            filters = new ArrayList<>();
        }

        if(bindingResult.hasErrors()){
            throw new TaskException(ErrorMessageMaker.formErrorMessage(bindingResult));
        }

        List<Task> tasks = tasksService.getTasks(user.getId(), filters);

        return tasks.stream().map(this::convertToTaskDTO).toList();
    }

    @PostMapping("/get-all/pageable")
    @ResponseStatus(HttpStatus.OK)
    public PageDTO<TaskDTO> getAllTasksWithPagination(
            @RequestParam(name = "numberOfPage") Integer numberOfPage,
            @RequestParam(name = "itemsPerPage") Integer itemsPerPage,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = false,
                    description = "Next operators are allowed: <br/>- CONTAINS, " +
                            "<br/>- EQUALS" +
                            "<br/>- LESS_THAN" +
                            "<br/>- GREATER_THAN" +
                            "<br/>- CONTAINS_IGNORE_CASE" +
                            "<br/>- EQUALS_IGNORE_CASE" +
                            "<br/>- NONE(if you want only sort with current field)<br/><br/>" +
                            "You do not have to put user filter into list, if you do this, you will get an error message."
            )
            @Valid @RequestBody(required = false) List<FilterTask> filters, BindingResult bindingResult
    ){
        User user = userDetailsHolder.getUserFromSecurityContext();

        if(filters == null){
            filters = new ArrayList<>();
        }

        if(bindingResult.hasErrors()){
            throw new TaskException(ErrorMessageMaker.formErrorMessage(bindingResult));
        }

        Page<Task> page = tasksService.getTasks(user.getId(), filters, numberOfPage, itemsPerPage);
        return convertToPageDTO(page);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TaskDTO> getOneTask(@PathVariable("id") String taskId){
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

    @PatchMapping("/{id}/update")
    @ResponseStatus(HttpStatus.OK)
    public TaskDTO updateTask(@PathVariable(name = "id") String taskId, @RequestBody @Valid TaskDTO taskDTO, BindingResult bindingResult){
        Task updatedTask = convertToTask(taskDTO);

        taskValidator.validate(updatedTask, bindingResult);

        if(bindingResult.hasErrors()){
            throw new TaskException(ErrorMessageMaker.formErrorMessage(bindingResult));
        }

        return convertToTaskDTO(tasksService.update(taskId, updatedTask, userDetailsHolder.getUserFromSecurityContext()));
    }

    @DeleteMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable(name = "id") String id){
        tasksService.delete(id, userDetailsHolder.getUserFromSecurityContext());
    }

    @PutMapping("/{id}/set-completed")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setCompleted(@PathVariable(name = "id") String id,
                             @RequestParam(name = "completed") Boolean completed){
        tasksService.setCompleted(id, completed, userDetailsHolder.getUserFromSecurityContext());
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

    private PageDTO<TaskDTO> convertToPageDTO(Page<Task> page){
        PageDTO<TaskDTO> pageDTO = new PageDTO<>();
        pageDTO.setCurrentPage(page.getNumber());
        pageDTO.setNumberOfElements(page.getNumberOfElements());
        pageDTO.setPageSize(page.getSize());
        pageDTO.setTotalPages(page.getTotalPages());
        pageDTO.setTotalElements(page.getTotalElements());
        pageDTO.setContent(page.stream().map(this::convertToTaskDTO).toList());

        return pageDTO;
    }
}
