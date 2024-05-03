package com.sotnikov.ListToDoBackend.controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.sotnikov.ListToDoBackend.exceptions.ErrorsMap;
import com.sotnikov.ListToDoBackend.exceptions.NotRegisteredException;
import com.sotnikov.ListToDoBackend.exceptions.UserDataNotChangedException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleException(NotRegisteredException e){
        return getResponseEntityMap(e);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleException(UserDataNotChangedException e){
        return getResponseEntityMap(e);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleException(UsernameNotFoundException e){
        return getResponseEntityString(e);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleException(BadCredentialsException e){
        return getResponseEntityString(e);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, Object>> handleException(JWTVerificationException e){
        return new ResponseEntity<>(Map.of("errors", "JWT token is incorrect"), HttpStatus.BAD_REQUEST);
    }



    private ResponseEntity<Map<String, Object>> getResponseEntityMap(ErrorsMap e){
        return new ResponseEntity<>(
                Map.of("errors", e.get()),
                HttpStatus.BAD_REQUEST
        );
    }
    private ResponseEntity<Map<String, Object>> getResponseEntityString(Exception e){
        return new ResponseEntity<>(
                Map.of("error", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }
}
