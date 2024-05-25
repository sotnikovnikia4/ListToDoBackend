package com.sotnikov.ListToDoBackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreationSubtaskDTO {
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;
}
