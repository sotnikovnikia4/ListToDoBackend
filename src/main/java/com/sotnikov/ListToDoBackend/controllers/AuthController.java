package com.sotnikov.ListToDoBackend.controllers;

import com.sotnikov.ListToDoBackend.dto.RegistrationDTO;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.util.RegistrationValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private RegistrationValidator registrationValidator;

    private ModelMapper modelMapper;

    @PostMapping("/registration")
    public ResponseEntity<Map<String,Object>> register(@RequestBody RegistrationDTO registrationDTO,
                                                       BindingResult bindingResult){//TODO

        User user = convertToUser(registrationDTO);
        registrationValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()){

        }


        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(){

        return null;
    }

    private User convertToUser(RegistrationDTO registrationDTO){
        return modelMapper.map(registrationDTO, User.class);
    }
}
