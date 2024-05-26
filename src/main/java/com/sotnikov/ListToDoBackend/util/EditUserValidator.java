package com.sotnikov.ListToDoBackend.util;

import com.sotnikov.ListToDoBackend.config.UserDetailsHolder;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EditUserValidator implements Validator {

    private final UsersService usersService;

    private final UserDetailsHolder userDetailsHolder;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(User.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User updatedUser = (User)target;
        Optional<User> userWithSameLogin = usersService.findOne(updatedUser.getLogin());
        User currentUser = userDetailsHolder.getUserFromSecurityContext();

        if(userWithSameLogin.isPresent() && !Objects.equals(userWithSameLogin.get().getLogin(), currentUser.getLogin())){
            errors.rejectValue("login", "", "This login is already occupied");
        }
    }
}
