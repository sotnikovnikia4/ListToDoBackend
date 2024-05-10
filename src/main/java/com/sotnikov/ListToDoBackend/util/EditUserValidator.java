package com.sotnikov.ListToDoBackend.util;

import com.sotnikov.ListToDoBackend.exceptions.UserDataNotChangedException;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import com.sotnikov.ListToDoBackend.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EditUserValidator implements Validator {

    private final UsersService usersService;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(User.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User updatedUser = (User)target;
        Optional<User> userWithSameLogin = usersService.findOne(updatedUser.getLogin());
        String loginOfCurrentUser = ((UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        if(userWithSameLogin.isPresent() && !Objects.equals(userWithSameLogin.get().getLogin(), loginOfCurrentUser)){
            errors.rejectValue("login", "", "This login is already occupied");
        }
    }
}
