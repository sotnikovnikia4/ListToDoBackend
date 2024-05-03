package com.sotnikov.ListToDoBackend.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticationDTO {

    @JsonProperty("login")
    @NotBlank
    private String login;

    @JsonProperty("password")
    @NotBlank
    private String password;
}
