package com.sotnikov.ListToDoBackend.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Document("tasks")
public class Task{
    @Id
    private String id;

    private String name;
    private String description;
    private LocalDateTime deadline;
    private String tag;
    private Integer priority;
    private boolean completed;

    private List<Subtask> subtasks;

    private String userId;
}
