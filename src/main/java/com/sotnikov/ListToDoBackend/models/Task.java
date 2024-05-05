package com.sotnikov.ListToDoBackend.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Task {
    private String name;
    private String description;
    private int level;
    private List<Task> tasks;
}
