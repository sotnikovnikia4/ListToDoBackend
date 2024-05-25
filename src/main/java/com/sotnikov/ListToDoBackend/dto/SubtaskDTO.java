package com.sotnikov.ListToDoBackend.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SubtaskDTO {
    private String name;
    private String description;
    private boolean completed;
}
