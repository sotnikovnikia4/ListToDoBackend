package com.sotnikov.ListToDoBackend.controllers;

import com.sotnikov.ListToDoBackend.security.UserDetailsHolder;
import com.sotnikov.ListToDoBackend.dto.UserDTO;
import com.sotnikov.ListToDoBackend.exceptions.UserDataNotChangedException;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.services.UsersService;
import com.sotnikov.ListToDoBackend.util.EditUserValidator;
import com.sotnikov.ListToDoBackend.util.ErrorMessageMaker;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@SecurityScheme(type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", name = "bearerAuth")
@SecurityRequirement(name = "bearerAuth")
public class UsersController {

    private final ModelMapper modelMapper;
    private final EditUserValidator editUserValidator;
    private final UsersService usersService;

    private final UserDetailsHolder userDetailsHolder;

    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO get(){
        return convertToUserDTO(userDetailsHolder.getUserFromSecurityContext());
    }

    @PatchMapping("/edit")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO edit(@Valid @RequestBody UserDTO userDTO, BindingResult bindingResult){
        User newUserData = convertToUser(userDTO);
        editUserValidator.validate(newUserData, bindingResult);

        if(bindingResult.hasErrors()){
            throw new UserDataNotChangedException(ErrorMessageMaker.formErrorMessage(bindingResult));
        }

        User currentUser = userDetailsHolder.getUserFromSecurityContext();
        newUserData.setId(currentUser.getId());

        return convertToUserDTO(usersService.update(newUserData));
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

    private UserDTO convertToUserDTO(User user){
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setPassword(null);

        return userDTO;
    }
}
