package com.sotnikov.ListToDoBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {

    private String id;

    private String name;
    private String description;
    private LocalDateTime deadline;
    private String tag;
    private Integer priority;
    private boolean completed;

    private List<SubtaskDTO> subtasks;
}
