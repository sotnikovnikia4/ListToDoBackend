package com.sotnikov.ListToDoBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PageDTO<T> {
    private Integer currentPage;
    private Integer numberOfElements;
    private Integer pageSize;
    private Integer totalPages;
    private Long totalElements;
    private List<T> content;
}
