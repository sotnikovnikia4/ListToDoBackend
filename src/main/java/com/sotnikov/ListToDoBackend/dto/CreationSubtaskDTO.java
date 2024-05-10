package com.sotnikov.ListToDoBackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreationSubtaskDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;
}
