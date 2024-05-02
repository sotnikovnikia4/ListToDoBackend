package com.sotnikov.ListToDoBackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final UserValidator userValidator;

    private final JWTUtil jwtUtil;
    private final ModelMapper modelMapper;

    private final RegistrationService registrationService;
    private final AuthenticationManager authManager;

    @PostMapping("/registration")
    public ResponseEntity<Object> register(){
//
        return null;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthenticationDTO authenticationDTO){
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                authenticationDTO.getPhoneNumber(),
                authenticationDTO.getPassword()
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authManager.authenticate(authToken).getPrincipal();

        String token = jwtUtil.generateToken(userDetails.getUser().getId().toString(), userDetails.getUser().getPhoneNumber());

        AuthResponse response = new AuthResponse("Успешная авторизация", ResponseStatus.SUCCESS, token);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private User convertToUser(UserRequestDTO userRequestDTO){
        return modelMapper.map(userRequestDTO, User.class);
    }



    @ExceptionHandler
    public ResponseEntity<AuthResponse> handleException(UserNotRegisteredException e){
        AuthResponse response = new AuthResponse(e.getMessage(), ResponseStatus.NOT_REGISTERED, "");

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(AuthenticationException e){
        return new ResponseEntity<>("Неверный номер телефона или пароль", HttpStatus.BAD_REQUEST);
    }
}
