package com.sotnikov.ListToDoBackend.exceptions;

import lombok.Getter;

import java.util.Map;

public class UserDataNotChangedException extends RuntimeException{

    public UserDataNotChangedException(String message){
        super(message);
    }
}
