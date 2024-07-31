package com.sotnikov.ListToDoBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class FilterTask {
    private String field;
    private String operator;
    private String value;
}
