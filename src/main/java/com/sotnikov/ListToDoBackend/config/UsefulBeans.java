package com.sotnikov.ListToDoBackend.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UsefulBeans {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
