package com.sotnikov.ListToDoBackend.controllers;

import com.sotnikov.ListToDoBackend.dto.AuthenticationDTO;
import com.sotnikov.ListToDoBackend.dto.RegistrationDTO;
import com.sotnikov.ListToDoBackend.exceptions.NotRegisteredException;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.AuthManagerImpl;
import com.sotnikov.ListToDoBackend.security.JWTUtil;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import com.sotnikov.ListToDoBackend.services.RegistrationService;
import com.sotnikov.ListToDoBackend.util.ErrorMessageMaker;
import com.sotnikov.ListToDoBackend.util.RegistrationValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    private final RegistrationValidator registrationValidator;
    private final RegistrationService registrationService;
    private final JWTUtil jwtUtil;

    private final AuthManagerImpl authManager;

    private final ModelMapper modelMapper;

    @PostMapping("/registration")
    public ResponseEntity<Map<String,Object>> register(@RequestBody RegistrationDTO registrationDTO,
                                                       BindingResult bindingResult){

        User user = convertToUser(registrationDTO);
        registrationValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()){
            throw new NotRegisteredException(ErrorMessageMaker.formErrorMap(bindingResult).toString());
        }

        registrationService.register(user);

        String token = jwtUtil.generateToken(user);

        return new ResponseEntity<>(
                Map.of("token", token),
                HttpStatus.OK
        );
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody AuthenticationDTO authenticationDTO, BindingResult bindingResult){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                authenticationDTO.getLogin(),
                authenticationDTO.getPassword()
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authManager.authenticate(authToken).getPrincipal();

        String token = jwtUtil.generateToken(userDetails.getUser());

        return new ResponseEntity<>(Map.of("token", token), HttpStatus.OK);
    }

    private User convertToUser(RegistrationDTO registrationDTO){
        return modelMapper.map(registrationDTO, User.class);
    }
}
