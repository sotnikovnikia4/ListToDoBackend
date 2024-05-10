package com.sotnikov.ListToDoBackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreationTaskDTO {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private LocalDateTime deadline;

    private String tag;

    @NotNull
    @Min(0)
    @Max(10)
    private Integer priority;

    @NotNull
    private List<CreationSubtaskDTO> subtasks;
}
