package com.tunduh.timemanagement.utils.pagination;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class CustomPagination<T> {
    List<T> content;
    CustomPageable pageable;

    public CustomPagination(Page<T> page) {
        this.content = page.getContent();
        this.pageable = new CustomPageable(page.getPageable().getPageNumber(),
                page.getPageable().getPageSize(), page.getTotalElements());
    }

    @Data
    static class CustomPageable {
        int pageNumber;
        int pageSize;
        long totalElements;

        public CustomPageable(int pageNumber, int pageSize, long totalElements) {

            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            this.totalElements = totalElements;
        }
    }
}