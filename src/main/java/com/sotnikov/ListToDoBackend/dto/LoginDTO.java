package com.sotnikov.ListToDoBackend.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class LoginDTO {

    @JsonProperty("login")
    @NotBlank
    private String login;

    @JsonProperty("password")
    @NotBlank
    private String password;
}
