package com.sotnikov.ListToDoBackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreationTaskDTO {

    @NotBlank
    @JsonProperty("name")
    private String name;
    @NotNull
    @JsonProperty("description")
    private String description;

    @JsonProperty("parent_task_id")
    private String parentTask;
    @JsonProperty("child_tasks")
    private List<CreationTaskDTO> childTasks;
}
