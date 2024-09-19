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
        this.content = content;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = calculateTotalPages(totalElements, pageSize);
    }

    public CustomPagination(Page<T> page) {
        this.content = page.getContent();
        this.totalElements = page.getTotalElements();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalPages = page.getTotalPages();
    }

    private int calculateTotalPages(long totalElements, int pageSize) {
        return pageSize > 0 ? (int) Math.ceil((double) totalElements / (double) pageSize) : 0;
    }
}