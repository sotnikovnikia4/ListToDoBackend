package com.sotnikov.ListToDoBackend.util;

import com.sotnikov.ListToDoBackend.models.Task;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.UserDetailsHolder;
import com.sotnikov.ListToDoBackend.services.TasksService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TaskValidator implements Validator {
    private final TasksService tasksService;
    private final UserDetailsHolder userDetailsHolder;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Task.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Task task = (Task)target;

        User currentUser = userDetailsHolder.getUserFromSecurityContext();
        Task taskWithSameId = tasksService.getOne(task.getId(), userDetailsHolder.getUserFromSecurityContext());

        if(!Objects.equals(taskWithSameId.getUserId(), currentUser.getId())){
            errors.rejectValue("id", "", "The task does not belong the user");
        }
    }
}
