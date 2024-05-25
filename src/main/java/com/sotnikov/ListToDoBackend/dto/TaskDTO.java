package com.sotnikov.ListToDoBackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TaskDTO {

    private String id;

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

    private boolean completed;

    @NotNull
    private List<SubtaskDTO> subtasks;
}
