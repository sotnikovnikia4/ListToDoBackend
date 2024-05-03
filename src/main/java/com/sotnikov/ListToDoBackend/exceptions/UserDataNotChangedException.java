package com.sotnikov.ListToDoBackend.exceptions;

import lombok.Getter;

import java.util.Map;

public class UserDataNotChangedException extends RuntimeException implements ErrorsMap{
    @Getter
    private final Map<String, String> map;

    public UserDataNotChangedException(String message, Map<String, String> map){
        super(message);
        this.map = map;
    }

    @Override
    public Map<String, String> get() {
        return this.map;
    }
}
