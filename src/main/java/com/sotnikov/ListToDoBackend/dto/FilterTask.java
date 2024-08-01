package com.sotnikov.ListToDoBackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class FilterTask {
    @NotBlank
    private String field;
    @NotBlank
    private String operator;
    @NotBlank
    private String value;

    private boolean sorting = false;
    private boolean descendingOrder = false;
}
