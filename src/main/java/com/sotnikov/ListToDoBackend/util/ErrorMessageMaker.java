package com.sotnikov.ListToDoBackend.util;

import org.modelmapper.internal.Errors;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ErrorMessageMaker {
    private ErrorMessageMaker(){}

    public static Map<String, String> formErrorMap(BindingResult bindingResult){
        if(bindingResult == null) return Collections.emptyMap();

        Map<String, String> map = new HashMap<>();

        for(FieldError error : bindingResult.getFieldErrors()){
            map.put(error.getField(), error.getDefaultMessage());
        }

        return map;
    }

    public static String formErrorMessage(BindingResult bindingResult){
        if(bindingResult == null) return "";

        StringBuilder sb = new StringBuilder();

        for(FieldError error : bindingResult.getFieldErrors()){
            sb.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }

        sb.setLength(sb.length() - 2);

        return sb.toString();
    }
}
