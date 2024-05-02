package com.sotnikov.ListToDoBackend.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public class RegistrationDTO {

    @JsonProperty("login")
    private String login;

    @JsonProperty("password")
    private String password;

    @JsonProperty("name")
    private String name;
}
