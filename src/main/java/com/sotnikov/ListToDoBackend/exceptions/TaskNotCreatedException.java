package com.sotnikov.ListToDoBackend.exceptions;

public class TaskNotCreatedException extends RuntimeException{
    public TaskNotCreatedException(String message){
        super(message);
    }
}
