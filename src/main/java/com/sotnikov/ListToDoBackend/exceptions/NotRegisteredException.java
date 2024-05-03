package com.sotnikov.ListToDoBackend.exceptions;

public class NotRegisteredException extends RuntimeException{
    public NotRegisteredException(String message){
        super(message);
    }
}
