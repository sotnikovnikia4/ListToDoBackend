package com.sotnikov.ListToDoBackend.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subtask {//TODO
    private String name;
    private String description;
    private boolean completed;

    @Transient
    private Task task;
}
