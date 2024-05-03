package com.sotnikov.ListToDoBackend.util;

import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RegistrationValidator implements Validator {
    private final UsersService usersService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(User.class);
    }

    @Override
    public void validate(Object target, Errors errors) {//TODO
        User userToCheck = (User)target;
        Optional<User> userWithSameLogin = usersService.findOne(userToCheck.getLogin());

        if(userWithSameLogin.isPresent()){
            errors.rejectValue("login", "", "Пользователь с таким логином уже существует!");
        }
    }
}
