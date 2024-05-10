package com.sotnikov.ListToDoBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubtaskDTO {
    private String name;
    private String description;
    private boolean completed;
}
