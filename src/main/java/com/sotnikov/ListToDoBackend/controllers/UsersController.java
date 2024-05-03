package com.sotnikov.ListToDoBackend.controllers;

import com.sotnikov.ListToDoBackend.dto.UserDTO;
import com.sotnikov.ListToDoBackend.exceptions.UserDataNotChangedException;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import com.sotnikov.ListToDoBackend.services.UsersService;
import com.sotnikov.ListToDoBackend.util.ErrorMessageMaker;
import com.sotnikov.ListToDoBackend.util.RegistrationValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UsersController {

    private final ModelMapper modelMapper;
    private final RegistrationValidator registrationValidator;
    private final UsersService usersService;

    @PatchMapping("/edit")
    public void edit(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult){
        User newUserData = convertToUser(userDTO);
        registrationValidator.validate(newUserData, bindingResult);
        if(bindingResult.hasErrors()){
            throw new UserDataNotChangedException("User data is not changed", ErrorMessageMaker.formErrorMap(bindingResult));
        }

        User currentUser = ((UserDetailsImpl)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        usersService.update(currentUser, newUserData);
    }

    private User convertToUser(UserDTO userDTO){
        return modelMapper.map(userDTO, User.class);
    }
}
