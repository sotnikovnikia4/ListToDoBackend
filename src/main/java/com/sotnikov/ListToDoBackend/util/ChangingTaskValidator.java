package com.sotnikov.ListToDoBackend.util;

import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import com.sotnikov.ListToDoBackend.services.TasksService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChangingTaskValidator implements Validator {
    private final TasksService tasksService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Task.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Task task = (Task)target;

        User currentUser = ((UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Optional<Task> taskWithSameId = tasksService.getOne(task.getId());

        if(taskWithSameId.isEmpty()){
            errors.rejectValue("id", "", "The task does not exist");
        }

        if(!Objects.equals(task.getUserId(), currentUser.getId().toString())){
            errors.rejectValue("userId", "", "The task does not belong the user");
        }
    }
}
