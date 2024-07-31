package com.sotnikov.ListToDoBackend.exceptions;

public class TaskException extends RuntimeException{
    public TaskException(String message){
        super(message);
    }

    public TaskException(Exception e){
        super(e);
    }
}
