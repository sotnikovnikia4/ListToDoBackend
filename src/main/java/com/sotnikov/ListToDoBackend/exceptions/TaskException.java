package com.sotnikov.ListToDoBackend.exceptions;

public class TaskException extends RuntimeException{
    public TaskException(String message){
        super(message);
    }
}
