package com.sotnikov.ListToDoBackend.util;

import com.sotnikov.ListToDoBackend.security.UserDetailsHolder;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.services.UsersService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EditUserValidatorTest {

    @Mock
    private UsersService usersService;

    @Mock
    private UserDetailsHolder userDetailsHolder;

    @InjectMocks
    private EditUserValidator validator;

    private User userToValidate;
    private Errors errors;

    @BeforeEach
    void setUp(){
        User authenticated = User.builder()
                .name("John")
                .login("123")
                .password("123")
                .build();

        userToValidate = User.builder()
                .name("Tom")
                .login("123")
                .password("12345")
                .build();

        errors = new BeanPropertyBindingResult(userToValidate, "userToValidate");

        when(userDetailsHolder.getUserFromSecurityContext()).thenReturn(authenticated);
    }

    @Test
    void validateWhenNotSameLogin() {
        User userToValidate = User.builder()
                .name("Tom")
                .login("123")
                .password("12345")
                .build();

        when(usersService.findOne(userToValidate.getLogin())).thenReturn(Optional.empty());

        validator.validate(userToValidate, errors);

        Assertions.assertFalse(errors.hasErrors());
    }

    @Test
    void validateWhenFoundWithSameLogin() {
        User userInDB = User.builder()
                .name("John")
                .login("123")
                .password("123")
                .build();

        when(usersService.findOne(userToValidate.getLogin())).thenReturn(Optional.ofNullable(userInDB));

        validator.validate(userToValidate, errors);

        Assertions.assertFalse(errors.hasErrors());
    }

    @Test
    void validateWhenFoundWithSameLoginOfAnother() {
        User userInDB = User.builder()
                .name("John")
                .login("1234")
                .password("123")
                .build();

        userToValidate.setLogin("1234");

        when(usersService.findOne(userToValidate.getLogin())).thenReturn(Optional.ofNullable(userInDB));

        validator.validate(userToValidate, errors);

        Assertions.assertTrue(errors.hasErrors());
    }
}