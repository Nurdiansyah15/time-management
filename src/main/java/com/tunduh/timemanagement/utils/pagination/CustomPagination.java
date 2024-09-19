package com.tunduh.timemanagement.utils.pagination;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class CustomPagination<T> {
    private List<T> content;
    private long totalElements;
    private int pageNumber;
    private int pageSize;
    private int totalPages;

    public CustomPagination(List<T> content, long totalElements, int pageNumber, int pageSize) {
        this(content, totalElements, pageNumber, pageSize, calculateTotalPages(totalElements, pageSize));
    }

    public CustomPagination(Page<T> page) {
        this(page.getContent(), page.getTotalElements(), page.getNumber(), page.getSize(), page.getTotalPages());
    }

    private CustomPagination(List<T> content, long totalElements, int pageNumber, int pageSize, int totalPages) {
        this.content = content;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
    }

    private static int calculateTotalPages(long totalElements, int pageSize) {
        return (pageSize > 0) ? (int) Math.ceil((double) totalElements / pageSize) : 0;
    }
}
