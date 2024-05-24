package com.sotnikov.ListToDoBackend.models;

import lombok.*;
import org.springframework.data.annotation.Transient;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subtask {
    private String name;
    private String description;
    private boolean completed;

    @Transient
    private Task task;
}
