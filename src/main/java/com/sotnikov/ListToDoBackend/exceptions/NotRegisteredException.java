package com.sotnikov.ListToDoBackend.exceptions;

import lombok.Getter;

import java.util.Map;

public class NotRegisteredException extends RuntimeException{

    public NotRegisteredException(String message){
        super(message);
    }
}
