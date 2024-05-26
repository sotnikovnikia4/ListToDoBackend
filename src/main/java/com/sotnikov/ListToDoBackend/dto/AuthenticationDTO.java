package com.sotnikov.ListToDoBackend.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AuthenticationDTO {

    @JsonProperty("login")
    @NotBlank
    private String login;

    @JsonProperty("password")
    @NotBlank
    private String password;
}
