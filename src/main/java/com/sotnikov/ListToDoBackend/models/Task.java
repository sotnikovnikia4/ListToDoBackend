package com.sotnikov.ListToDoBackend.models;

import com.sotnikov.ListToDoBackend.util.CascadeSave;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Document("tasks")
public class Task{
    @Id
    private String id;

    @NotBlank
    private String name;
    @NotNull
    private String description;
    private String userId;
    private int level;

    private String parentTask;
    @DBRef
    @CascadeSave
    private List<Task> childTasks;
}
