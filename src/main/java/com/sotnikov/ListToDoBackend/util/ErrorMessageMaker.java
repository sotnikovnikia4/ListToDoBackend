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
}
