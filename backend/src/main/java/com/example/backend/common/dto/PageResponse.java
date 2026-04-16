package com.example.backend.common.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T> (
    List<T> content,
    boolean empty,
    boolean first,
    boolean last,
    int pageNumber,
    int size,
    int totalPages,
    long totalElements
) {
    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.isEmpty(),
                page.isFirst(),
                page.isLast(),
                page.getPageable().getPageNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
