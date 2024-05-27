package com.toyota.salesservice.dto.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Custom pageable for storing details
 */

@Data
@NoArgsConstructor
public class CustomPageable {
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;

    public CustomPageable(int pageNumber, int pageSize, int totalPages, long totalElements) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
