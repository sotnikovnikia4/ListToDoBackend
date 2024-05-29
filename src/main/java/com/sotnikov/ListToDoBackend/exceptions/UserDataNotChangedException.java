package com.sotnikov.ListToDoBackend.exceptions;

public class UserDataNotChangedException extends RuntimeException{

    public UserDataNotChangedException(String message){
        super(message);
    }
}
