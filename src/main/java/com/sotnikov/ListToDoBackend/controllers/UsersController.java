package com.sotnikov.ListToDoBackend.controllers;

import com.sotnikov.ListToDoBackend.config.UserDetailsHolder;
import com.sotnikov.ListToDoBackend.dto.UserDTO;
import com.sotnikov.ListToDoBackend.exceptions.UserDataNotChangedException;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import com.sotnikov.ListToDoBackend.services.UsersService;
import com.sotnikov.ListToDoBackend.util.EditUserValidator;
import com.sotnikov.ListToDoBackend.util.ErrorMessageMaker;
import com.sotnikov.ListToDoBackend.util.RegistrationValidator;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UsersController {

    private final ModelMapper modelMapper;
    private final EditUserValidator editUserValidator;
    private final UsersService usersService;

    private final PasswordEncoder passwordEncoder;

    private final UserDetailsHolder userDetailsHolder;

    @PatchMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    public void edit(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult){
        User newUserData = convertToUser(userDTO);
        editUserValidator.validate(newUserData, bindingResult);

        if(bindingResult.hasErrors()){
            throw new UserDataNotChangedException("User data is not changed, invalid data");
        }

        newUserData.setPassword(passwordEncoder.encode(newUserData.getPassword()));

        User currentUser = userDetailsHolder.getUserFromSecurityContext();
        usersService.update(newUserData, currentUser.getId());
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.OK)
    public void delete(){
        User currentUser = userDetailsHolder.getUserFromSecurityContext();

        usersService.delete(currentUser);
    }

    private User convertToUser(UserDTO userDTO){
        return modelMapper.map(userDTO, User.class);
    }
}
