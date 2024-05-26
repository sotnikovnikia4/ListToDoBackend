package com.sotnikov.ListToDoBackend.controllers;

import com.sotnikov.ListToDoBackend.dto.AuthenticationDTO;
import com.sotnikov.ListToDoBackend.dto.TokenDTO;
import com.sotnikov.ListToDoBackend.dto.UserDTO;
import com.sotnikov.ListToDoBackend.exceptions.NotRegisteredException;
import com.sotnikov.ListToDoBackend.models.User;
import com.sotnikov.ListToDoBackend.security.JWTUtil;
import com.sotnikov.ListToDoBackend.security.UserDetailsImpl;
import com.sotnikov.ListToDoBackend.services.RegistrationService;
import com.sotnikov.ListToDoBackend.util.ErrorMessageMaker;
import com.sotnikov.ListToDoBackend.util.RegistrationValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationValidator registrationValidator;
    private final RegistrationService registrationService;
    private final JWTUtil jwtUtil;

    private final AuthenticationManager authManager;

    private final ModelMapper modelMapper;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TokenDTO> register(@RequestBody @Valid UserDTO userDTO,
                                          BindingResult bindingResult){

        User user = convertToUser(userDTO);
        registrationValidator.validate(user, bindingResult);

        if(bindingResult.hasErrors()){
            throw new NotRegisteredException(ErrorMessageMaker.formErrorMessage(bindingResult));
        }

        registrationService.register(user);

        TokenDTO token = TokenDTO.builder()
                .token(jwtUtil.generateToken(user))
                .build();

        return new ResponseEntity<>(
                token,
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody AuthenticationDTO authenticationDTO, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new UsernameNotFoundException(ErrorMessageMaker.formErrorMessage(bindingResult));
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                authenticationDTO.getLogin(),
                authenticationDTO.getPassword()
        );

        Authentication authentication = authManager.authenticate(authToken);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        TokenDTO token = TokenDTO.builder()
                .token(jwtUtil.generateToken(userDetails.getUser()))
                .build();

        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    private User convertToUser(UserDTO userDTO){
        return modelMapper.map(userDTO, User.class);
    }
}
