package com.sotnikov.ListToDoBackend.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Task {
    private String id;
    private String name;
    private String description;
    private UUID userId;
    private int level;
    private List<Task> tasks;
}
